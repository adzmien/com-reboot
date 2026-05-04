package com.reboot.uam.lib.exception;

/**
 * Thrown when a request lacks valid authentication credentials. Maps to HTTP 401.
 */
public class UnauthorizedException extends RebootException {

    /**
     * @param code    error code, e.g. {@code AUTH-001}
     * @param message description of why authentication failed
     */
    public UnauthorizedException(String code, String message) {
        super(code, message);
    }
}
