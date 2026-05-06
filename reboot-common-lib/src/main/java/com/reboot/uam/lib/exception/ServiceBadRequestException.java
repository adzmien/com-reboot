package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the incoming request is malformed or fails business validation.
 * Maps to HTTP 400 Bad Request.
 */
public class ServiceBadRequestException extends RebootException {

    public ServiceBadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}
