package com.trading.control.application.domain.exception;

/**
 * Raised when the downstream market-data-service cannot be reached or returns a
 * server-side failure. Maps to HTTP 503.
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
