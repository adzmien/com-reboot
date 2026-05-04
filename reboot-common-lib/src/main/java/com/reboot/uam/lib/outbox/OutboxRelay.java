package com.reboot.uam.lib.outbox;

/**
 * Contract for the transactional outbox relay component.
 * Implementations scan the outbox table for unsent events and publish them to the
 * message broker, guaranteeing at-least-once delivery without distributed transactions.
 */
public interface OutboxRelay {

    /**
     * Scans pending outbox records and relays them to the message broker.
     * Called on a fixed schedule; implementations must be idempotent.
     */
    void relay();
}
