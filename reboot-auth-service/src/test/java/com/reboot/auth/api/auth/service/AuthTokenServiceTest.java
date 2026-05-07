package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// LENIENT: setUp stubs opsForValue() but not every test uses it
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    private static final String SECRET = "reboot-uam-super-secret-key-that-is-long-enough-for-hs256";

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    private AuthTokenServiceImpl authTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret(SECRET);
        props.setAccessTokenTtlMinutes(15);
        props.setRefreshTokenTtlDays(7);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        authTokenService = new AuthTokenServiceImpl(props, redisTemplate);
    }

    @Test
    void issueAccessToken_returnsNonBlankJwt() {
        String token = authTokenService.issueAccessToken(1L, "user@test.com", "ADMIN");
        assertThat(token).isNotBlank();
        // JWT has three dot-separated parts
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void issueRefreshToken_storesUserIdInRedisAndReturnsToken() {
        String token = authTokenService.issueRefreshToken(42L);

        assertThat(token).isNotBlank();
        verify(valueOps).set(eq("refresh:token:" + token), eq("42"), any());
    }

    @Test
    void getUserIdFromRefreshToken_returnsUserIdWhenPresent() {
        when(valueOps.get("refresh:token:abc")).thenReturn("99");

        Long userId = authTokenService.getUserIdFromRefreshToken("abc");

        assertThat(userId).isEqualTo(99L);
    }

    @Test
    void getUserIdFromRefreshToken_returnsNullWhenAbsent() {
        when(valueOps.get("refresh:token:missing")).thenReturn(null);

        Long userId = authTokenService.getUserIdFromRefreshToken("missing");

        assertThat(userId).isNull();
    }

    @Test
    void revokeRefreshToken_deletesKeyFromRedis() {
        authTokenService.revokeRefreshToken("tok123");

        verify(redisTemplate).delete("refresh:token:tok123");
    }
}
