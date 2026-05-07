package com.reboot.auth.api.auth.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

/**
 * Response body for a successful {@code POST /auth/refresh}.
 */
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RefreshResponse {

    private final String accessToken;
}
