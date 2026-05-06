package com.reboot.uam.api;

import com.reboot.uam.lib.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a test extending {@link BaseIntegrationTest} can write to and read
 * from the K8s-hosted MariaDB (via NodePort) without additional setup.
 */
class BaseIntegrationIT extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void can_write_and_read_from_mariadb() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS it_probe (id BIGINT AUTO_INCREMENT PRIMARY KEY, value VARCHAR(255))"
        );
        jdbcTemplate.update("INSERT INTO it_probe (value) VALUES (?)", "hello-mariadb");

        String result = jdbcTemplate.queryForObject(
                "SELECT value FROM it_probe WHERE value = ?", String.class, "hello-mariadb"
        );

        assertThat(result).isEqualTo("hello-mariadb");

        jdbcTemplate.execute("DROP TABLE IF EXISTS it_probe");
    }
}
