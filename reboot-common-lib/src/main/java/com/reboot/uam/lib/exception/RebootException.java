package com.reboot.uam.lib.exception;

/**
 * Base exception for all domain-specific exceptions in the Reboot UAM platform.
 * Carries a platform-scoped error {@code code} (e.g. {@code UAM-001}) so that
 * the global exception handler can produce a structured {@code ApiResponse} without
 * any additional service-level metadata.
 */
public abstract class RebootException extends RuntimeException {

    private final String code;

    /**
     * @param code    flat error code, e.g. {@code UAM-001}
     * @param message human-readable error description
     */
    protected RebootException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * @param code    flat error code, e.g. {@code UAM-001}
     * @param message human-readable error description
     * @param cause   original exception that triggered this one
     */
    protected RebootException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /** Returns the platform-scoped error code. */
    public String getCode() {
        return code;
    }
}
