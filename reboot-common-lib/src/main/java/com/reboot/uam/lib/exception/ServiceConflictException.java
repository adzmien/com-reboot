package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a resource conflict prevents the request from completing.
 * Maps to HTTP 409 Conflict.
 */
public class ServiceConflictException extends RebootException {

    public ServiceConflictException(String message, String errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}
