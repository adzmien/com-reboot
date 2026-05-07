package com.reboot.auth.api.auth.model.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Response body for a successful {@code POST /auth/refresh}.
 */
@Getter
@Builder
public class RefreshResponse {

    private final String accessToken;
}
