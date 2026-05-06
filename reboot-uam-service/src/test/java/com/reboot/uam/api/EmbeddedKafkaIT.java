package com.reboot.uam.api;

import com.reboot.uam.lib.test.EmbeddedKafkaTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a test extending {@link EmbeddedKafkaTest} can publish and consume
 * a Kafka message without additional setup.
 */
@EmbeddedKafka(topics = EmbeddedKafkaIT.TOPIC, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@Import(EmbeddedKafkaIT.KafkaTestListener.class)
class EmbeddedKafkaIT extends EmbeddedKafkaTest {

    static final String TOPIC = "it-test-topic";

    static final CountDownLatch LATCH = new CountDownLatch(1);
    static volatile String receivedMessage;

    @TestConfiguration
    static class KafkaTestListener {
        @KafkaListener(topics = EmbeddedKafkaIT.TOPIC, groupId = "it-test-group")
        public void consume(String message) {
            receivedMessage = message;
            LATCH.countDown();
        }
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void can_publish_and_consume_kafka_message() throws InterruptedException {
        kafkaTemplate.send(TOPIC, "test-key", "hello-kafka");

        boolean received = LATCH.await(10, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(receivedMessage).isEqualTo("hello-kafka");
    }
}
