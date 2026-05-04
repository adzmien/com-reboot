package com.reboot.uam.lib.exception;

import com.reboot.uam.lib.common.ErrorDetail.ViolationItem;

import java.util.List;

/**
 * Thrown when multiple business rule violations occur simultaneously. Maps to HTTP 422.
 * Each violation is captured as a {@link ViolationItem} with a code and a message.
 */
public class BusinessRuleViolationsException extends BusinessRuleException {

    private final List<ViolationItem> violations;

    /**
     * @param code       top-level error code
     * @param message    summary message
     * @param violations list of individual business rule violations
     */
    public BusinessRuleViolationsException(String code, String message, List<ViolationItem> violations) {
        super(code, message);
        this.violations = List.copyOf(violations);
    }

    /** Returns the individual business rule violations. */
    public List<ViolationItem> getViolations() {
        return violations;
    }
}
