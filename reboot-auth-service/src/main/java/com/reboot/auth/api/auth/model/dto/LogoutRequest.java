package com.reboot.auth.api.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for {@code POST /auth/logout}.
 */
@Getter
@Setter
public class LogoutRequest {

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
