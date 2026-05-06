package com.reboot.uam.lib.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Unified API response envelope for all REST endpoints.
 *
 * @param <T> the type of the response data payload
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;

    /**
     * Creates a successful response wrapping the given data.
     *
     * @param data the response payload
     * @param <T>  the payload type
     * @return a successful {@link ApiResponse}
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    /**
     * Creates an error response with the given code and human-readable message.
     *
     * @param errorCode service-scoped error code, e.g. {@code "AUTH-001"}
     * @param message   human-readable description of the error
     * @param <T>       the payload type (will be {@code null})
     * @return an error {@link ApiResponse}
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }
}
