package com.idc.interview.model;

/**
 * Describes which country and timescale should be rendered into a table.
 */
public record MarketShareRequest(String country, String timescale) {
    public MarketShareRequest {
        country = requireNonBlank(country, "country");
        timescale = requireNonBlank(timescale, "timescale");
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}
