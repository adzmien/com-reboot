package com.reboot.uam.lib.errorcode;

/**
 * Error code constants for the auth-service.
 * Format: {@code AUTH-NNN}.
 */
public final class AuthErrorCodes {

    /** Account is locked due to too many failed attempts. */
    public static final String ACCOUNT_LOCKED = "AUTH-001";

    /** Invalid credentials supplied. */
    public static final String INVALID_CREDENTIALS = "AUTH-002";

    /** Token is expired or invalid. */
    public static final String INVALID_TOKEN = "AUTH-003";

    private AuthErrorCodes() {
        // utility class
    }
}
