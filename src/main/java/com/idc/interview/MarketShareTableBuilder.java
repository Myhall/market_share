package com.idc.interview;

import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.RawMarketRecord;
import com.idc.interview.model.TableMetadata;
import com.idc.interview.model.VendorResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds immutable market-share tables from validated input rows.
 */
final class MarketShareTableBuilder {
    private static final Logger LOGGER = Logger.getLogger(MarketShareTableBuilder.class.getName());
    private static final String TABLE_NUMBER = "1";
    private static final String TABLE_TITLE = "PC Quarterly Market Share";
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final Pattern TIMESCALE_PATTERN = Pattern.compile("(\\d{4})\\s+Q([1-4])");

    public MarketShareTable build(List<RawMarketRecord> input, MarketShareRequest request) {
        Objects.requireNonNull(input, "input must not be null.");
        Objects.requireNonNull(request, "request must not be null.");

        LOGGER.info(() -> "Building table for country='" + request.country() + "', timescale='" + request.timescale() + "'");

        List<RawMarketRecord> matchingRows = input.stream()
                .filter(row -> row.country().equals(request.country()) && row.timescale().equals(request.timescale()))
                .toList();

        if (matchingRows.isEmpty()) {
            throw new MarketShareException(
                    "No data found for country='%s', timescale='%s'.".formatted(request.country(), request.timescale())
            );
        }

        Map<String, RawMarketRecord> uniqueByVendor = new LinkedHashMap<>();
        for (RawMarketRecord rawRecord : matchingRows) {
            RawMarketRecord previous = uniqueByVendor.putIfAbsent(normalizeKey(rawRecord.vendor()), rawRecord);
            if (previous != null) {
                throw new MarketShareException(
                        "Duplicate vendor entry detected for country='%s', timescale='%s', vendor='%s'."
                                .formatted(rawRecord.country(), rawRecord.timescale(), rawRecord.vendor())
                );
            }
        }

        List<VendorResult> vendorRows = new ArrayList<>(uniqueByVendor.size());
        long totalUnits = 0;
        for (RawMarketRecord rawRecord : uniqueByVendor.values()) {
            long normalizedUnits = normalizeUnits(rawRecord);
            vendorRows.add(new VendorResult(rawRecord.vendor(), normalizedUnits, BigDecimal.ZERO));
            try {
                totalUnits = Math.addExact(totalUnits, normalizedUnits);
            } catch (ArithmeticException exception) {
                throw new MarketShareException("Total units exceed the supported range for the selected table.", exception);
            }
        }

        if (totalUnits <= 0) {
            throw new MarketShareException("No positive unit data remains after rounding to whole units.");
        }

        long finalizedTotalUnits = totalUnits;
        List<VendorResult> finalizedRows = vendorRows.stream()
                .map(row -> new VendorResult(row.vendor(), row.units(), calculateShare(row.units(), finalizedTotalUnits)))
                .toList();

        TableMetadata metadata = new TableMetadata(
                TABLE_NUMBER,
                TABLE_TITLE,
                request.country(),
                toTimescaleLabel(request.timescale())
        );
        VendorResult totalRow = new VendorResult("Total", finalizedTotalUnits, ONE_HUNDRED);
        return new MarketShareTable(metadata, finalizedRows, totalRow);
    }

    private long normalizeUnits(RawMarketRecord rawRecord) {
        try {
            return rawRecord.units().setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (ArithmeticException exception) {
            throw new MarketShareException(
                    "Units value is outside the supported range for vendor '%s': %s"
                            .formatted(rawRecord.vendor(), rawRecord.units().toPlainString()),
                    exception
            );
        }
    }

    private BigDecimal calculateShare(long units, long totalUnits) {
        return BigDecimal.valueOf(units)
                .multiply(ONE_HUNDRED)
                .divide(BigDecimal.valueOf(totalUnits), 1, RoundingMode.HALF_UP);
    }

    private String toTimescaleLabel(String timescale) {
        Matcher matcher = TIMESCALE_PATTERN.matcher(timescale);
        if (!matcher.matches()) {
            return timescale;
        }
        return matcher.group(2) + "Q" + matcher.group(1).substring(2);
    }

    private String normalizeKey(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
