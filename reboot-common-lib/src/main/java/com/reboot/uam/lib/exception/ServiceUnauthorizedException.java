package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when authentication credentials are missing, invalid, or expired.
 * Maps to HTTP 401 Unauthorized.
 */
public class ServiceUnauthorizedException extends RebootException {

    public ServiceUnauthorizedException(String message, String errorCode) {
        super(message, HttpStatus.UNAUTHORIZED, errorCode);
    }
}
