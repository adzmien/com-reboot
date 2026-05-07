package com.reboot.auth.api.auth.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for {@code POST /auth/refresh}.
 */
@Getter
@Setter
public class RefreshRequest {

    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
