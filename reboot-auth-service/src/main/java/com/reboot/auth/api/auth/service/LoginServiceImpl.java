package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.auth.model.dto.LoginRequest;
import com.reboot.auth.api.auth.model.dto.LoginResponse;
import com.reboot.auth.api.auth.model.dto.RefreshResponse;
import com.reboot.auth.api.auth.model.entity.InternalUser;
import com.reboot.auth.api.auth.repository.InternalUserRepository;
import com.reboot.uam.lib.errorcode.AuthErrorCodes;
import com.reboot.uam.lib.exception.ServiceLockedException;
import com.reboot.uam.lib.exception.ServiceUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link LoginService}.
 * Orchestrates lockout checks, credential validation, and token issuance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private static final String INVALID_CREDENTIALS_MSG = "Invalid email or password.";
    private static final String ACCOUNT_LOCKED_MSG = "Account is temporarily locked due to too many failed attempts.";
    private static final String INVALID_TOKEN_MSG = "Refresh token is expired or invalid.";

    private final InternalUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final AccountLockoutService accountLockoutService;

    @Override
    public LoginResponse login(LoginRequest request) {
        InternalUser user = userRepository.findByEmailAndDeletedFalse(request.getEmail())
                .orElse(null);

        // Check lockout before password verification when the user is found
        if (user != null && accountLockoutService.isLocked(user.getId())) {
            throw new ServiceLockedException(ACCOUNT_LOCKED_MSG, AuthErrorCodes.ACCOUNT_LOCKED);
        }

        // Use the same invalid-credentials exception for both unknown email and wrong
        // password — prevents user enumeration (acceptance criterion AC-2)
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getHashedPassword())) {
            if (user != null) {
                accountLockoutService.recordFailedAttempt(user.getId());
                // Re-check lock after incrementing so a threshold-crossing attempt
                // on this very request is surfaced as 423, not 401
                if (accountLockoutService.isLocked(user.getId())) {
                    throw new ServiceLockedException(ACCOUNT_LOCKED_MSG, AuthErrorCodes.ACCOUNT_LOCKED);
                }
            }
            throw new ServiceUnauthorizedException(INVALID_CREDENTIALS_MSG, AuthErrorCodes.INVALID_CREDENTIALS);
        }

        accountLockoutService.resetAttempts(user.getId());
        log.info("Successful login. userId={}, email={}", user.getId(), user.getEmail());

        String accessToken = authTokenService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = authTokenService.issueRefreshToken(user.getId());

        Boolean forceChange = user.isForcePasswordChange() ? Boolean.TRUE : null;
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .forcePasswordChange(forceChange)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        authTokenService.revokeRefreshToken(refreshToken);
        log.info("User logged out.");
    }

    @Override
    public RefreshResponse refresh(String refreshToken) {
        Long userId = authTokenService.getUserIdFromRefreshToken(refreshToken);
        if (userId == null) {
            throw new ServiceUnauthorizedException(INVALID_TOKEN_MSG, AuthErrorCodes.INVALID_TOKEN);
        }

        InternalUser user = userRepository.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ServiceUnauthorizedException(INVALID_TOKEN_MSG, AuthErrorCodes.INVALID_TOKEN));

        String newAccessToken = authTokenService.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        log.info("Access token refreshed. userId={}", userId);

        return RefreshResponse.builder().accessToken(newAccessToken).build();
    }
}
