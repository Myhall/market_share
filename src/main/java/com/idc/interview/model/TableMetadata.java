package com.idc.interview.model;

/**
 * Descriptive values rendered with the final table.
 */
public record TableMetadata(String tableNumber, String title, String country, String timescaleLabel) {
    public TableMetadata {
        tableNumber = requireNonBlank(tableNumber, "tableNumber");
        title = requireNonBlank(title, "title");
        country = requireNonBlank(country, "country");
        timescaleLabel = requireNonBlank(timescaleLabel, "timescaleLabel");
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}
