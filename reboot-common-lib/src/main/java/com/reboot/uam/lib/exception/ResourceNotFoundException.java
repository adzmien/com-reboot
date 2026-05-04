package com.reboot.uam.lib.exception;

/**
 * Thrown when a requested resource does not exist. Maps to HTTP 404.
 */
public class ResourceNotFoundException extends RebootException {

    /**
     * @param code    error code, e.g. {@code UAM-010}
     * @param message description identifying the missing resource
     */
    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }
}
