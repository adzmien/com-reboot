package com.reboot.auth.api.auth.service;

import com.reboot.auth.api.auth.model.dto.LoginRequest;
import com.reboot.auth.api.auth.model.dto.LoginResponse;
import com.reboot.auth.api.auth.model.dto.RefreshResponse;

/**
 * Orchestrates the full authentication lifecycle:
 * login, logout, and access-token refresh.
 */
public interface LoginService {

    /**
     * Validates credentials and issues tokens on success.
     * Throws {@link com.reboot.uam.lib.exception.ServiceLockedException} (423) when
     * the account is locked, and {@link com.reboot.uam.lib.exception.ServiceUnauthorizedException}
     * (401) for invalid credentials — the same exception is used for unknown email and
     * wrong password to prevent user enumeration.
     *
     * @param request the login credentials
     * @return a {@link LoginResponse} containing the access and refresh tokens
     */
    LoginResponse login(LoginRequest request);

    /**
     * Invalidates the supplied refresh token, effectively logging the user out.
     *
     * @param refreshToken the opaque refresh-token value to revoke
     */
    void logout(String refreshToken);

    /**
     * Exchanges a valid refresh token for a new access token.
     * Throws {@link com.reboot.uam.lib.exception.ServiceUnauthorizedException} (401)
     * when the token is expired or unknown.
     *
     * @param refreshToken the opaque refresh-token value
     * @return a {@link RefreshResponse} containing the new access token
     */
    RefreshResponse refresh(String refreshToken);
}
