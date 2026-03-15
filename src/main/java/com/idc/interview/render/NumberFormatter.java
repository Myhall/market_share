package com.idc.interview.render;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Formatting helpers for rendered unit and share values.
 */
public final class NumberFormatter {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private NumberFormatter() {
    }

    public static String formatUnits(long units) {
        NumberFormat formatter = NumberFormat.getIntegerInstance(Locale.US);
        formatter.setGroupingUsed(true);
        return formatter.format(units);
    }

    public static String formatShare(BigDecimal sharePercentage) {
        Objects.requireNonNull(sharePercentage, "sharePercentage must not be null.");
        if (sharePercentage.compareTo(ONE_HUNDRED) == 0) {
            return "100%";
        }

        return sharePercentage.setScale(1, RoundingMode.HALF_UP).toPlainString() + "%";
    }
}
