package com.reboot.auth.api;

import org.junit.jupiter.api.Test;

/**
 * Placeholder test for the auth-service compile check.
 * Full integration tests ({@code *IT.java}) require Testcontainers MariaDB.
 */
class RebootAuthApplicationTest {

    @Test
    void applicationClassExists() {
        // Verifies the main application class compiles and can be referenced.
        Class<?> clazz = RebootAuthApplication.class;
        assert clazz != null;
    }
}
