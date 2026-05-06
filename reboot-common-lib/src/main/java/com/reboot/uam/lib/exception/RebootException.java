package com.reboot.uam.lib.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract base for all domain exceptions in the Reboot platform.
 * Every service-specific exception must extend one of the concrete subclasses
 * declared in this hierarchy, never this class directly.
 */
@Getter
public abstract class RebootException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    protected RebootException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
