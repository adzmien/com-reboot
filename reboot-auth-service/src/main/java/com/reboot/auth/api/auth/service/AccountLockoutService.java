package com.reboot.auth.api.auth.service;

/**
 * Manages the failed-login attempt counter and account-lock state.
 * Redis is the primary store; DB is the fallback when Redis is unavailable.
 */
public interface AccountLockoutService {

    /**
     * Returns {@code true} if the account is currently locked.
     *
     * @param userId the user's database ID
     * @return {@code true} when the account is locked, {@code false} otherwise
     */
    boolean isLocked(Long userId);

    /**
     * Records a failed login attempt and locks the account when the configured
     * threshold is reached.
     *
     * @param userId the user's database ID
     */
    void recordFailedAttempt(Long userId);

    /**
     * Resets the failed-attempt counter after a successful login.
     *
     * @param userId the user's database ID
     */
    void resetAttempts(Long userId);
}
