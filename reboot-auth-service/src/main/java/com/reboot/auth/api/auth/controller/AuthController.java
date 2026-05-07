package com.reboot.auth.api.auth.controller;

import com.reboot.auth.api.auth.model.dto.*;
import com.reboot.auth.api.auth.service.LoginService;
import com.reboot.uam.lib.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 * All responses are wrapped in {@link ApiResponse}.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    /**
     * Authenticates a user and returns access + refresh tokens.
     *
     * @param request the login credentials
     * @return 200 with tokens, 401 for bad credentials, 423 when account is locked
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * Revokes the supplied refresh token, ending the session.
     *
     * @param request contains the refresh token to invalidate
     * @return 200 on success
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        loginService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /**
     * Issues a new access token for a valid refresh token.
     *
     * @param request contains the refresh token
     * @return 200 with a new access token, 401 when the token is expired or unknown
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse response = loginService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
