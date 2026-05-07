package com.reboot.auth.api.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests for all auth endpoints.
 * Uses Testcontainers MariaDB (with Flyway migrations) and Redis.
 * Does NOT extend BaseIntegrationTest — Flyway must run here.
 * Skipped automatically when Docker daemon is not running.
 */
@EnabledIf("dockerAvailable")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIT {

    static boolean dockerAvailable() {
        return new File("/var/run/docker.sock").exists();
    }

    @Container
    static final MariaDBContainer<?> MARIADB =
            new MariaDBContainer<>(DockerImageName.parse("mariadb:10.11"))
                    .withDatabaseName("uam_auth")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @Container
    static final RedisContainer REDIS =
            new RedisContainer(DockerImageName.parse("redis:7-alpine"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MARIADB::getJdbcUrl);
        registry.add("spring.datasource.username", MARIADB::getUsername);
        registry.add("spring.datasource.password", MARIADB::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379).toString());
        // Disable Kafka auto-connect in tests
        registry.add("spring.autoconfigure.exclude",
                () -> "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String ADMIN_EMAIL = "admin@reboot.local";
    private static final String ADMIN_PASSWORD = "Admin@2024!";
    private static final String LOGIN_URL = "/auth/login";
    private static final String LOGOUT_URL = "/auth/logout";
    private static final String REFRESH_URL = "/auth/refresh";

    // ── AC-1: valid credentials return 200 with tokens ──────────────────────

    @Test
    void ac1_validCredentials_return200WithTokens() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.access_token").isNotEmpty())
                .andExpect(jsonPath("$.data.refresh_token").isNotEmpty())
                .andReturn();

        JsonNode data = parseData(result);
        assertThat(data.get("access_token").asText()).isNotBlank();
        assertThat(data.get("refresh_token").asText()).isNotBlank();
    }

    // ── AC-2: invalid password returns 401 (same as unknown email) ──────────

    @Test
    void ac2_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, "WrongPassword!")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("AUTH-002"));
    }

    @Test
    void ac2_unknownEmail_returns401SameAsWrongPassword() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("nobody@reboot.local", "irrelevant")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTH-002"));
    }

    // ── AC-3: after N failed attempts account is locked → 423 ───────────────

    @Test
    void ac3_afterMaxFailedAttempts_returns423() throws Exception {
        String email = ADMIN_EMAIL;
        // Exhaust the lockout counter (default 5 attempts)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody(email, "bad-pass")))
                    .andReturn();
        }
        // Next attempt (wrong password) must return 423 or 401 depending on whether
        // the 5th attempt itself triggered the lock
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(email, "bad-pass")))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value("AUTH-001"));
    }

    // ── AC-4: locked account returns 423 even with correct password ──────────
    // NOTE: This test shares state with ac3; run order matters — both hit the
    // same seeded admin. In isolation this is a separate test class concern.
    // We use a dedicated helper that explicitly reaches lock state first.

    @Test
    void ac4_lockedAccount_correctPasswordStillReturns423() throws Exception {
        // First lock the account via repeated failures
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(ADMIN_EMAIL, "bad")));
        }
        // Correct password must still return 423
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value("AUTH-001"));
    }

    // ── AC-5: successful login resets counter ────────────────────────────────

    @Test
    void ac5_successfulLogin_resetsCounter() throws Exception {
        // 4 failures (below threshold)
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(ADMIN_EMAIL, "bad")));
        }
        // One successful login
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk());

        // One more failure — should return 401, not 423 (counter was reset)
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, "bad")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTH-002"));
    }

    // ── AC-6: logout revokes token, refresh then returns 401 ─────────────────

    @Test
    void ac6_logoutThenRefresh_returns401() throws Exception {
        String refreshToken = loginAndGetRefreshToken();

        mockMvc.perform(post(LOGOUT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTH-003"));
    }

    // ── AC-7: refresh with valid token returns new access token ──────────────

    @Test
    void ac7_refreshWithValidToken_returnsNewAccessToken() throws Exception {
        String refreshToken = loginAndGetRefreshToken();

        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.access_token").isNotEmpty());
    }

    // ── AC-8: refresh with unknown token returns 401 ─────────────────────────

    @Test
    void ac8_refreshWithUnknownToken_returns401() throws Exception {
        mockMvc.perform(post(REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"totally-unknown-token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTH-003"));
    }

    // ── AC-9: force_password_change flag included when true ──────────────────

    @Test
    void ac9_firstLogin_forcePasswordChangeIncluded() throws Exception {
        // The seeded admin has force_password_change = TRUE
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = parseData(result);
        assertThat(data.path("force_password_change").asBoolean(false)).isTrue();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private String loginBody(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    private String loginAndGetRefreshToken() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
        return parseData(result).get("refresh_token").asText();
    }

    private JsonNode parseData(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data");
    }
}
