package com.reboot.auth.api.auth.service;

/**
 * Issues and validates JWTs; manages refresh-token lifecycle in Redis.
 */
public interface AuthTokenService {

    /**
     * Issues a signed JWT access token containing userId, email, and role claims.
     *
     * @param userId the user's database ID
     * @param email  the user's email
     * @param role   the user's role
     * @return a compact, signed JWT string
     */
    String issueAccessToken(Long userId, String email, String role);

    /**
     * Generates a cryptographically random refresh token and stores it in Redis
     * with a TTL equal to {@code jwt.refresh-token-ttl-days}.
     *
     * @param userId the user ID to associate with the refresh token
     * @return the opaque refresh-token value
     */
    String issueRefreshToken(Long userId);

    /**
     * Looks up the user ID stored under the given refresh token.
     *
     * @param refreshToken the opaque refresh-token value
     * @return the associated user ID, or {@code null} if not found / expired
     */
    Long getUserIdFromRefreshToken(String refreshToken);

    /**
     * Deletes a refresh token from Redis, effectively invalidating it.
     *
     * @param refreshToken the token to revoke
     */
    void revokeRefreshToken(String refreshToken);
}
