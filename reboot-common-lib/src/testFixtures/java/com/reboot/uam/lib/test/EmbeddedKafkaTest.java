package com.reboot.uam.lib.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

/**
 * Base class for tests that need an in-process Kafka broker.
 * <p>
 * Spring's {@link EmbeddedKafka} annotation starts a lightweight Kafka broker
 * inside the test JVM and automatically sets {@code spring.kafka.bootstrap-servers}
 * to the embedded broker address via {@code bootstrapServersProperty}.
 * <p>
 * JPA, datasource, and Flyway auto-configuration are excluded so that the test
 * context starts without a live database — this base class is for Kafka-only tests.
 * Subclasses that also need a database should extend {@link BaseIntegrationTest} instead.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
        }
)
@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
public abstract class EmbeddedKafkaTest {
    // Subclasses inject KafkaTemplate<?, ?> or @KafkaListener beans as needed.
}
