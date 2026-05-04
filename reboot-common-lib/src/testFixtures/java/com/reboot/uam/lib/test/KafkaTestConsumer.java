package com.reboot.uam.lib.test;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Synchronous Kafka consumer utility for integration tests. Creates an isolated
 * consumer group per instance so parallel tests do not share offsets.
 */
public final class KafkaTestConsumer implements AutoCloseable {

    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(10);

    private final KafkaConsumer<String, String> consumer;

    public KafkaTestConsumer(String bootstrapServers, String topic) {
        consumer = new KafkaConsumer<>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        ));
        consumer.subscribe(List.of(topic));
    }

    /**
     * Polls until at least {@code expectedCount} records arrive or the timeout elapses.
     *
     * @param expectedCount minimum number of records to collect
     * @return list of collected records (may be fewer than expected if timeout expires)
     */
    public List<ConsumerRecord<String, String>> poll(int expectedCount) {
        List<ConsumerRecord<String, String>> collected = new ArrayList<>();
        long deadline = System.currentTimeMillis() + POLL_TIMEOUT.toMillis();

        while (collected.size() < expectedCount && System.currentTimeMillis() < deadline) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            records.forEach(collected::add);
        }

        return collected;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
