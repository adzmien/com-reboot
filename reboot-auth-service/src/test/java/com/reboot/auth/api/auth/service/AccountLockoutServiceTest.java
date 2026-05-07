package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.auth.model.entity.InternalUser;
import com.reboot.auth.api.auth.repository.InternalUserRepository;
import com.reboot.auth.api.config.LockoutProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// LENIENT: setUp stubs opsForValue() globally but not every test calls through it
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class AccountLockoutServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;
    @Mock private InternalUserRepository userRepository;

    private AccountLockoutServiceImpl service;

    @BeforeEach
    void setUp() {
        LockoutProperties props = new LockoutProperties();
        props.setMaxAttempts(5);
        props.setLockDurationMinutes(30);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        service = new AccountLockoutServiceImpl(redisTemplate, userRepository, props);
    }

    // ── isLocked ────────────────────────────────────────────────────────────

    @Test
    void isLocked_returnsFalseWhenKeyAbsent() {
        when(redisTemplate.hasKey("login:locked:1")).thenReturn(false);
        assertThat(service.isLocked(1L)).isFalse();
    }

    @Test
    void isLocked_returnsTrueWhenKeyPresent() {
        when(redisTemplate.hasKey("login:locked:1")).thenReturn(true);
        assertThat(service.isLocked(1L)).isTrue();
    }

    @Test
    void isLocked_fallsBackToDbWhenRedisFails() {
        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis down"));
        InternalUser user = lockedUser(LocalDateTime.now().plusMinutes(10));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(service.isLocked(1L)).isTrue();
    }

    @Test
    void isLocked_dbFallback_returnsFalseWhenLockExpired() {
        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis down"));
        InternalUser user = lockedUser(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(service.isLocked(1L)).isFalse();
    }

    // ── recordFailedAttempt ─────────────────────────────────────────────────

    @Test
    void recordFailedAttempt_incrementsCounter() {
        when(valueOps.increment("login:attempts:1")).thenReturn(1L);

        service.recordFailedAttempt(1L);

        verify(valueOps).increment("login:attempts:1");
        verify(redisTemplate).expire(eq("login:attempts:1"), any());
    }

    @Test
    void recordFailedAttempt_locksAccountOnThreshold() {
        when(valueOps.increment("login:attempts:1")).thenReturn(5L);
        InternalUser user = activeUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.recordFailedAttempt(1L);

        verify(valueOps).set(eq("login:locked:1"), eq("1"), any());
    }

    @Test
    void recordFailedAttempt_doesNotLockBeforeThreshold() {
        when(valueOps.increment("login:attempts:1")).thenReturn(3L);

        service.recordFailedAttempt(1L);

        verify(valueOps, never()).set(eq("login:locked:1"), any(), any());
    }

    // ── resetAttempts ───────────────────────────────────────────────────────

    @Test
    void resetAttempts_deletesRedisKeysAndClearsDb() {
        InternalUser user = activeUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.resetAttempts(1L);

        verify(redisTemplate).delete("login:attempts:1");
        verify(redisTemplate).delete("login:locked:1");
        assertThat(user.getFailedLoginAttempts()).isZero();
        assertThat(user.getLockedUntil()).isNull();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private InternalUser activeUser() {
        InternalUser u = new InternalUser();
        u.setFailedLoginAttempts(0);
        return u;
    }

    private InternalUser lockedUser(LocalDateTime lockedUntil) {
        InternalUser u = new InternalUser();
        u.setLockedUntil(lockedUntil);
        return u;
    }
}
