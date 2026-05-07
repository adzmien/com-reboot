package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.auth.model.dto.LoginRequest;
import com.reboot.auth.api.auth.model.dto.LoginResponse;
import com.reboot.auth.api.auth.model.dto.RefreshResponse;
import com.reboot.auth.api.auth.model.entity.InternalUser;
import com.reboot.auth.api.auth.repository.InternalUserRepository;
import com.reboot.uam.lib.exception.ServiceLockedException;
import com.reboot.uam.lib.exception.ServiceUnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock private InternalUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthTokenService authTokenService;
    @Mock private AccountLockoutService accountLockoutService;

    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginServiceImpl(userRepository, passwordEncoder, authTokenService, accountLockoutService);
    }

    // ── login happy path ─────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsTokens() {
        InternalUser user = buildUser(false);
        when(userRepository.findByEmailAndDeletedFalse("admin@test.com")).thenReturn(Optional.of(user));
        when(accountLockoutService.isLocked(1L)).thenReturn(false);
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(authTokenService.issueAccessToken(1L, "admin@test.com", "ADMIN")).thenReturn("access-tok");
        when(authTokenService.issueRefreshToken(1L)).thenReturn("refresh-tok");

        LoginResponse resp = loginService.login(loginRequest("admin@test.com", "secret"));

        assertThat(resp.getAccessToken()).isEqualTo("access-tok");
        assertThat(resp.getRefreshToken()).isEqualTo("refresh-tok");
        assertThat(resp.getForcePasswordChange()).isNull();
        verify(accountLockoutService).resetAttempts(1L);
    }

    @Test
    void login_forcePasswordChange_includedInResponse() {
        InternalUser user = buildUser(true);
        when(userRepository.findByEmailAndDeletedFalse("admin@test.com")).thenReturn(Optional.of(user));
        when(accountLockoutService.isLocked(1L)).thenReturn(false);
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(authTokenService.issueAccessToken(any(), any(), any())).thenReturn("tok");
        when(authTokenService.issueRefreshToken(any())).thenReturn("rtok");

        LoginResponse resp = loginService.login(loginRequest("admin@test.com", "secret"));

        assertThat(resp.getForcePasswordChange()).isTrue();
    }

    // ── login failure: invalid credentials ──────────────────────────────────

    @Test
    void login_unknownEmail_throws401() {
        when(userRepository.findByEmailAndDeletedFalse("bad@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login(loginRequest("bad@test.com", "pw")))
                .isInstanceOf(ServiceUnauthorizedException.class);
    }

    @Test
    void login_wrongPassword_throws401() {
        InternalUser user = buildUser(false);
        when(userRepository.findByEmailAndDeletedFalse("admin@test.com")).thenReturn(Optional.of(user));
        when(accountLockoutService.isLocked(1L)).thenReturn(false);
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);
        when(accountLockoutService.isLocked(1L)).thenReturn(false); // after increment still not locked

        assertThatThrownBy(() -> loginService.login(loginRequest("admin@test.com", "wrong")))
                .isInstanceOf(ServiceUnauthorizedException.class);
        verify(accountLockoutService).recordFailedAttempt(1L);
    }

    // ── login failure: locked account ────────────────────────────────────────

    @Test
    void login_lockedAccount_throws423() {
        InternalUser user = buildUser(false);
        when(userRepository.findByEmailAndDeletedFalse("admin@test.com")).thenReturn(Optional.of(user));
        when(accountLockoutService.isLocked(1L)).thenReturn(true);

        assertThatThrownBy(() -> loginService.login(loginRequest("admin@test.com", "secret")))
                .isInstanceOf(ServiceLockedException.class);
    }

    @Test
    void login_correctPasswordButLockedAccount_throws423() {
        InternalUser user = buildUser(false);
        when(userRepository.findByEmailAndDeletedFalse("admin@test.com")).thenReturn(Optional.of(user));
        when(accountLockoutService.isLocked(1L)).thenReturn(true);

        // Even with correct password, 423 must come first
        assertThatThrownBy(() -> loginService.login(loginRequest("admin@test.com", "secret")))
                .isInstanceOf(ServiceLockedException.class);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    // ── logout ───────────────────────────────────────────────────────────────

    @Test
    void logout_revokesRefreshToken() {
        loginService.logout("tok-xyz");
        verify(authTokenService).revokeRefreshToken("tok-xyz");
    }

    // ── refresh ──────────────────────────────────────────────────────────────

    @Test
    void refresh_validToken_returnsNewAccessToken() {
        InternalUser user = buildUser(false);
        when(authTokenService.getUserIdFromRefreshToken("rtok")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authTokenService.issueAccessToken(1L, "admin@test.com", "ADMIN")).thenReturn("new-access");

        RefreshResponse resp = loginService.refresh("rtok");

        assertThat(resp.getAccessToken()).isEqualTo("new-access");
    }

    @Test
    void refresh_expiredToken_throws401() {
        when(authTokenService.getUserIdFromRefreshToken("old")).thenReturn(null);

        assertThatThrownBy(() -> loginService.refresh("old"))
                .isInstanceOf(ServiceUnauthorizedException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private InternalUser buildUser(boolean forcePasswordChange) {
        InternalUser u = new InternalUser();
        u.setId(1L);
        u.setEmail("admin@test.com");
        u.setHashedPassword("hash");
        u.setRole("ADMIN");
        u.setActive(true);
        u.setForcePasswordChange(forcePasswordChange);
        u.setDeleted(false);
        return u;
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest r = new LoginRequest();
        r.setEmail(email);
        r.setPassword(password);
        return r;
    }
}
