package com.reboot.uam.lib.errorcode;

/**
 * Error code constants for the uam-service.
 * Format: {@code UAM-NNN}.
 */
public final class UamErrorCodes {

    /** Requested user not found. */
    public static final String USER_NOT_FOUND = "UAM-001";

    /** Duplicate email address. */
    public static final String EMAIL_ALREADY_EXISTS = "UAM-002";

    /** Requested role not found. */
    public static final String ROLE_NOT_FOUND = "UAM-003";

    /** Caller lacks permission to perform the operation. */
    public static final String ACCESS_DENIED = "UAM-004";

    /** Resource is locked (e.g. deactivated account). */
    public static final String ACCOUNT_INACTIVE = "UAM-005";

    private UamErrorCodes() {
        // utility class
    }
}
