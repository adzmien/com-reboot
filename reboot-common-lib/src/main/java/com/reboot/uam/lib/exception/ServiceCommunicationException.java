package com.reboot.uam.lib.exception;

/**
 * Thrown when an inter-service call fails or returns an unexpected response. Maps to HTTP 502.
 */
public class ServiceCommunicationException extends RebootException {

    /**
     * @param code    error code, e.g. {@code GW-010}
     * @param message description of the downstream failure
     */
    public ServiceCommunicationException(String code, String message) {
        super(code, message);
    }

    /**
     * @param code    error code, e.g. {@code GW-010}
     * @param message description of the downstream failure
     * @param cause   underlying exception from the Feign/HTTP client
     */
    public ServiceCommunicationException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
