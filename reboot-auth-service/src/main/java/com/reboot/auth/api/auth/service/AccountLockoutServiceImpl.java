package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.auth.model.entity.InternalUser;
import com.reboot.auth.api.auth.repository.InternalUserRepository;
import com.reboot.auth.api.config.LockoutProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Default implementation of {@link AccountLockoutService}.
 * <p>
 * Redis keys used:
 * <ul>
 *   <li>{@code login:attempts:{userId}} — INCR counter; expires after lock duration.</li>
 *   <li>{@code login:locked:{userId}}   — presence flag; TTL = lock duration.</li>
 * </ul>
 * If Redis throws, the DB columns {@code failed_login_attempts} and {@code locked_until}
 * are used as fallback.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountLockoutServiceImpl implements AccountLockoutService {

    private static final String ATTEMPTS_KEY_PREFIX = "login:attempts:";
    private static final String LOCKED_KEY_PREFIX = "login:locked:";
    private static final String LOCKED_VALUE = "1";

    private final StringRedisTemplate redisTemplate;
    private final InternalUserRepository userRepository;
    private final LockoutProperties lockoutProperties;

    @Override
    public boolean isLocked(Long userId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(LOCKED_KEY_PREFIX + userId));
        } catch (Exception redisEx) {
            log.warn("Redis unavailable for lock check; falling back to DB. userId={}", userId);
            return isLockedInDb(userId);
        }
    }

    @Override
    @Transactional
    public void recordFailedAttempt(Long userId) {
        try {
            recordFailedAttemptInRedis(userId);
        } catch (Exception redisEx) {
            log.warn("Redis unavailable for attempt recording; persisting to DB. userId={}", userId);
            recordFailedAttemptInDb(userId);
        }
    }

    @Override
    @Transactional
    public void resetAttempts(Long userId) {
        try {
            redisTemplate.delete(ATTEMPTS_KEY_PREFIX + userId);
            redisTemplate.delete(LOCKED_KEY_PREFIX + userId);
        } catch (Exception redisEx) {
            log.warn("Redis unavailable for attempt reset; clearing DB. userId={}", userId);
        }
        clearLockInDb(userId);
    }

    // ── Redis helpers ────────────────────────────────────────────────────────

    private void recordFailedAttemptInRedis(Long userId) {
        Duration lockDuration = Duration.ofMinutes(lockoutProperties.getLockDurationMinutes());
        String attemptsKey = ATTEMPTS_KEY_PREFIX + userId;

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        redisTemplate.expire(attemptsKey, lockDuration);

        log.info("Failed login attempt recorded. userId={}, attempts={}", userId, attempts);

        if (attempts != null && attempts >= lockoutProperties.getMaxAttempts()) {
            redisTemplate.opsForValue().set(LOCKED_KEY_PREFIX + userId, LOCKED_VALUE, lockDuration);
            persistLockToDb(userId, lockDuration);
            log.warn("Account locked. userId={}", userId);
        }
    }

    // ── DB helpers ───────────────────────────────────────────────────────────

    private boolean isLockedInDb(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getLockedUntil() != null && u.getLockedUntil().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    private void recordFailedAttemptInDb(Long userId) {
        Optional<InternalUser> opt = userRepository.findById(userId);
        if (opt.isEmpty()) {
            return;
        }
        InternalUser user = opt.get();
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= lockoutProperties.getMaxAttempts()) {
            LocalDateTime lockUntil = LocalDateTime.now()
                    .plusMinutes(lockoutProperties.getLockDurationMinutes());
            user.setLockedUntil(lockUntil);
            log.warn("Account locked via DB fallback. userId={}", userId);
        }
        userRepository.save(user);
    }

    private void persistLockToDb(Long userId, Duration lockDuration) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLockedUntil(LocalDateTime.now().plus(lockDuration));
            userRepository.save(user);
        });
    }

    private void clearLockInDb(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        });
    }
}
