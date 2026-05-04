package com.reboot.uam.lib.blacklist;

import java.time.Duration;

/**
 * Contract for the JWT token blacklist.
 * Implementations store revoked token JTIs so that the authentication filter can
 * reject them even before their natural expiry time.
 */
public interface TokenBlacklist {

    /**
     * Adds a token JTI to the blacklist with the given time-to-live.
     * The entry expires automatically after {@code ttl} has elapsed.
     *
     * @param jti unique JWT ID to revoke
     * @param ttl how long the entry should be retained (should match the token's remaining validity)
     */
    void add(String jti, Duration ttl);

    /**
     * Checks whether the given JTI has been revoked.
     *
     * @param jti JWT ID to check
     * @return {@code true} if the token has been blacklisted
     */
    boolean isBlacklisted(String jti);
}
