package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

/**
 * Default implementation of {@link AuthTokenService}.
 * Refresh tokens are stored as {@code refresh:token:{token} → userId} in Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private static final String REFRESH_KEY_PREFIX = "refresh:token:";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String issueAccessToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + Duration.ofMinutes(jwtProperties.getAccessTokenTtlMinutes()).toMillis());
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    @Override
    public String issueRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String redisKey = REFRESH_KEY_PREFIX + token;
        Duration ttl = Duration.ofDays(jwtProperties.getRefreshTokenTtlDays());
        redisTemplate.opsForValue().set(redisKey, String.valueOf(userId), ttl);
        log.info("Refresh token issued. userId={}", userId);
        return token;
    }

    @Override
    public Long getUserIdFromRefreshToken(String refreshToken) {
        String value = redisTemplate.opsForValue().get(REFRESH_KEY_PREFIX + refreshToken);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_KEY_PREFIX + refreshToken);
        log.info("Refresh token revoked.");
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
