package com.idc.interview.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * One validated CSV row before rounding and share calculations.
 */
public record RawMarketRecord(String country, String timescale, String vendor, BigDecimal units) {
    public RawMarketRecord {
        country = requireNonBlank(country, "country");
        timescale = requireNonBlank(timescale, "timescale");
        vendor = requireNonBlank(vendor, "vendor");
        Objects.requireNonNull(units, "units must not be null.");
        if (units.signum() < 0) {
            throw new IllegalArgumentException("units must not be negative.");
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}
