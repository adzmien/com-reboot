package com.reboot.auth.api.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end integration tests for all auth endpoints.
 * Connects to the remote K8s MariaDB (NodePort 30306) and Redis (NodePort 30379).
 * Flyway runs on startup so the internal_users table and admin seed are present.
 * Each test resets the admin lockout state in Redis and DB via @BeforeEach.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIT {

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        // Pin credentials explicitly — local env vars would otherwise shadow the K8s defaults
        registry.add("spring.datasource.username", () -> "rebootuser");
        registry.add("spring.datasource.password", () -> "abc@123");
        registry.add("spring.data.redis.password", () -> "abc@123");
        // Kafka is not needed for auth flow; skip auto-configuration to avoid connection errors
        registry.add("spring.autoconfigure.exclude",
                () -> "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration");
    }

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StringRedisTemplate redisTemplate;
    @Autowired JdbcTemplate jdbcTemplate;

    private static final String ADMIN_EMAIL    = "admin@reboot.local";
    private static final String ADMIN_PASSWORD = "Admin@2024!";
    private static final String LOGIN_URL      = "/auth/login";
    private static final String LOGOUT_URL     = "/auth/logout";
    private static final String REFRESH_URL    = "/auth/refresh";

    private Long adminId;

    @BeforeEach
    void resetAdminLockoutState() {
        adminId = jdbcTemplate.queryForObject(
                "SELECT id FROM internal_users WHERE email = ?", Long.class, ADMIN_EMAIL);
        // Clear Redis lockout keys so each test starts with a clean slate
        redisTemplate.delete("login:attempts:" + adminId);
        redisTemplate.delete("login:locked:" + adminId);
        // Reset DB lockout columns
        jdbcTemplate.update(
                "UPDATE internal_users SET failed_login_attempts = 0, locked_until = NULL WHERE id = ?",
                adminId);
    }

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

    // ── AC-2: invalid password/unknown email both return 401 AUTH-002 ────────

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

    // ── AC-3: after N failed attempts account is locked → 423 AUTH-001 ───────

    @Test
    void ac3_afterMaxFailedAttempts_returns423() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(ADMIN_EMAIL, "bad-pass")));
        }
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, "bad-pass")))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value("AUTH-001"));
    }

    // ── AC-4: locked account returns 423 even with correct password ──────────

    @Test
    void ac4_lockedAccount_correctPasswordStillReturns423() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(ADMIN_EMAIL, "bad-pass")));
        }
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value("AUTH-001"));
    }

    // ── AC-5: successful login resets counter ────────────────────────────────

    @Test
    void ac5_successfulLogin_resetsCounter() throws Exception {
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody(ADMIN_EMAIL, "bad-pass")));
        }
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk());

        // One failure after reset — must return 401, not 423
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, "bad-pass")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTH-002"));
    }

    // ── AC-6: logout revokes token; same token then returns 401 on refresh ───

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

    // ── AC-9: force_password_change flag present on first login ──────────────

    @Test
    void ac9_firstLogin_forcePasswordChangeFlagIsTrue() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(ADMIN_EMAIL, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(parseData(result).path("force_password_change").asBoolean(false)).isTrue();
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
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
    }
}
