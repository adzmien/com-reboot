package com.reboot.uam.lib.common;

import java.time.Instant;

/**
 * Standard envelope returned by every REST endpoint in the Reboot UAM platform.
 *
 * @param <T> payload type; {@code null} on error responses
 */
public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        ErrorDetail error,
        Instant timestamp
) {

    /**
     * Creates a successful response carrying the given payload.
     *
     * @param data    response payload
     * @param message human-readable success message
     * @param <T>     payload type
     * @return a success-flagged {@code ApiResponse}
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, Instant.now());
    }

    /**
     * Creates an error response without a payload.
     *
     * @param error   structured error detail
     * @param message top-level error message
     * @param <T>     phantom type parameter
     * @return a failure-flagged {@code ApiResponse}
     */
    public static <T> ApiResponse<T> error(ErrorDetail error, String message) {
        return new ApiResponse<>(false, null, message, error, Instant.now());
    }
}
