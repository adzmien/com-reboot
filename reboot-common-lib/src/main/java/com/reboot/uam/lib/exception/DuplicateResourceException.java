package com.reboot.uam.lib.exception;

/**
 * Thrown when an operation would create a duplicate of a unique resource. Maps to HTTP 409.
 */
public class DuplicateResourceException extends RebootException {

    /**
     * @param code    error code, e.g. {@code UAM-020}
     * @param message description of the conflicting resource
     */
    public DuplicateResourceException(String code, String message) {
        super(code, message);
    }
}
