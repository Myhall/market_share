package com.idc.interview;

/**
 * Runtime exception for invalid input data or unsupported export operations.
 */
public final class MarketShareException extends RuntimeException {
    public MarketShareException(String message) {
        super(message);
    }

    public MarketShareException(String message, Throwable cause) {
        super(message, cause);
    }
}
