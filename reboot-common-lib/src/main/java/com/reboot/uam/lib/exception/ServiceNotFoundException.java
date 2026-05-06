package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource does not exist.
 * Maps to HTTP 404 Not Found.
 */
public class ServiceNotFoundException extends RebootException {

    public ServiceNotFoundException(String message, String errorCode) {
        super(message, HttpStatus.NOT_FOUND, errorCode);
    }
}
