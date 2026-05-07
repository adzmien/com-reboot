package com.reboot.auth.api.auth.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

/**
 * Response body for a successful {@code POST /auth/login}.
 * {@code forcePasswordChange} is only included when {@code true}.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;

    /** Present and {@code true} only when the account requires an immediate password change. */
    private final Boolean forcePasswordChange;
}
