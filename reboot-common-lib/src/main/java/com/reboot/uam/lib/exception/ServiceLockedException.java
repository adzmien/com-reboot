package com.reboot.uam.lib.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a resource is temporarily locked (e.g. account lock-out).
 * Maps to HTTP 423 Locked.
 */
public class ServiceLockedException extends RebootException {

    public ServiceLockedException(String message, String errorCode) {
        super(message, HttpStatus.LOCKED, errorCode);
    }
}
