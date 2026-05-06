package com.reboot.uam.lib.web;

import com.reboot.uam.lib.exception.RebootException;
import com.reboot.uam.lib.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Base {@code @RestControllerAdvice} that maps every {@link RebootException}
 * subclass to an {@link ApiResponse} error envelope with the correct HTTP status.
 * <p>
 * Each service should declare its own {@code @RestControllerAdvice} that extends
 * this class so that service-specific exception handling can be added incrementally.
 */
@Slf4j
public abstract class BaseRestControllerAdvice {

    /**
     * Handles all {@link RebootException} subclasses and converts them to a
     * structured {@link ApiResponse} with the appropriate HTTP status code.
     *
     * @param ex the caught exception
     * @return a {@link ResponseEntity} carrying the error {@link ApiResponse}
     */
    @ExceptionHandler(RebootException.class)
    public ResponseEntity<ApiResponse<Void>> handleRebootException(RebootException ex) {
        log.warn("Business exception. errorCode={}, message={}", ex.getErrorCode(), ex.getMessage());
        ApiResponse<Void> body = ApiResponse.error(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(body);
    }
}
