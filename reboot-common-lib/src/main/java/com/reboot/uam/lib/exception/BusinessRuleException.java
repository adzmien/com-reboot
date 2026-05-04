package com.reboot.uam.lib.exception;

/**
 * Thrown when a single business rule is violated. Maps to HTTP 422.
 * For multiple simultaneous violations, use {@link BusinessRuleViolationsException}.
 */
public class BusinessRuleException extends RebootException {

    /**
     * @param code    error code, e.g. {@code UAM-030}
     * @param message description of the violated rule
     */
    public BusinessRuleException(String code, String message) {
        super(code, message);
    }
}
