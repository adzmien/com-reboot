package com.reboot.uam.lib.exception;

/**
 * Thrown when an authenticated user lacks the required permission. Maps to HTTP 403.
 */
public class ForbiddenException extends RebootException {

    /**
     * @param code    error code, e.g. {@code UAM-050}
     * @param message description of the access denial
     */
    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}
