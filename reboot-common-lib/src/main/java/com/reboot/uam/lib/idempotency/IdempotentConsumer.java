package com.reboot.uam.lib.idempotency;

/**
 * Contract for idempotent Kafka consumer support.
 * Implementations track processed message IDs so that duplicate deliveries
 * are detected and silently skipped rather than processed twice.
 */
public interface IdempotentConsumer {

    /**
     * Checks whether the given message has already been successfully processed.
     *
     * @param messageId unique identifier of the incoming message
     * @return {@code true} if the message was already processed
     */
    boolean isAlreadyProcessed(String messageId);

    /**
     * Records a message ID as successfully processed.
     * Must be called inside the same transaction as the business logic it protects.
     *
     * @param messageId unique identifier of the processed message
     */
    void markProcessed(String messageId);
}
