package com.reboot.uam.lib.common;

import java.util.List;

/**
 * Structured error payload included in {@link ApiResponse} when a request fails.
 *
 * @param code    flat error code (e.g. {@code UAM-001})
 * @param message human-readable summary of the error
 * @param details per-field or per-violation breakdown; may be empty
 */
public record ErrorDetail(String code, String message, List<ViolationItem> details) {

    /**
     * A single violation entry in the {@code details} list.
     * Used for both validation errors ({@code field} + {@code message})
     * and business rule violations ({@code code} + {@code message}).
     *
     * @param field   affected field name — {@code null} for business violations
     * @param code    violation code — {@code null} for field validation errors
     * @param message description of the violation
     */
    public record ViolationItem(String field, String code, String message) {

        /** Factory for Bean Validation field errors. */
        public static ViolationItem ofField(String field, String message) {
            return new ViolationItem(field, null, message);
        }

        /** Factory for business rule violation entries. */
        public static ViolationItem ofCode(String code, String message) {
            return new ViolationItem(null, code, message);
        }
    }
}
