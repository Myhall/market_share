package com.idc.interview.model;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Immutable market-share table with lookup and sorting helpers.
 */
public record MarketShareTable(TableMetadata metadata, List<VendorResult> vendorRows, VendorResult totalRow) {
    private static final Comparator<VendorResult> BY_VENDOR = Comparator
            .comparing(VendorResult::vendor, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(VendorResult::vendor);

    public MarketShareTable {
        Objects.requireNonNull(metadata, "metadata must not be null.");
        vendorRows = List.copyOf(Objects.requireNonNull(vendorRows, "vendorRows must not be null."));
        if (vendorRows.isEmpty()) {
            throw new IllegalArgumentException("vendorRows must not be empty.");
        }
        Objects.requireNonNull(totalRow, "totalRow must not be null.");
    }

    public Optional<VendorResult> findVendor(String vendor) {
        if (vendor == null || vendor.isBlank()) {
            return Optional.empty();
        }

        String normalizedVendor = normalizeKey(vendor);
        return vendorRows.stream()
                .filter(result -> normalizeKey(result.vendor()).equals(normalizedVendor))
                .findFirst();
    }

    public OptionalInt findVendorRow(String vendor) {
        if (vendor == null || vendor.isBlank()) {
            return OptionalInt.empty();
        }

        String normalizedVendor = normalizeKey(vendor);
        for (int index = 0; index < vendorRows.size(); index++) {
            if (normalizeKey(vendorRows.get(index).vendor()).equals(normalizedVendor)) {
                return OptionalInt.of(index + 1);
            }
        }
        return OptionalInt.empty();
    }

    public MarketShareTable sortedByVendor() {
        return sorted(BY_VENDOR);
    }

    public MarketShareTable sortedByUnitsAscending() {
        return sorted(Comparator.comparingLong(VendorResult::units).thenComparing(BY_VENDOR));
    }

    public MarketShareTable sortedByUnitsDescending() {
        return sorted(Comparator.comparingLong(VendorResult::units).reversed().thenComparing(BY_VENDOR));
    }

    private MarketShareTable sorted(Comparator<VendorResult> comparator) {
        return new MarketShareTable(metadata, vendorRows.stream().sorted(comparator).toList(), totalRow);
    }

    private static String normalizeKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
