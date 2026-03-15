package com.idc.interview.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * One rendered vendor row in the final table.
 */
public record VendorResult(String vendor, long units, BigDecimal sharePercentage) {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public VendorResult {
        vendor = requireNonBlank(vendor, "vendor");
        if (units < 0) {
            throw new IllegalArgumentException("units must not be negative.");
        }
        Objects.requireNonNull(sharePercentage, "sharePercentage must not be null.");
        if (sharePercentage.signum() < 0 || sharePercentage.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException("sharePercentage must be between 0 and 100.");
        }
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}
