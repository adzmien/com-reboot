package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the caller lacks permission for the requested operation.
 * Maps to HTTP 403 Forbidden.
 */
public class ServiceForbiddenException extends RebootException {

    public ServiceForbiddenException(String message, String errorCode) {
        super(message, HttpStatus.FORBIDDEN, errorCode);
    }
}
