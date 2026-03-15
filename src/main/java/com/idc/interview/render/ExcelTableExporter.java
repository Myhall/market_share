package com.idc.interview.render;

import com.idc.interview.model.MarketShareTable;
import java.util.Objects;

/**
 * Placeholder for a future spreadsheet export.
 */
public final class ExcelTableExporter {
    public String export(MarketShareTable table) {
        Objects.requireNonNull(table, "table must not be null.");
        throw new UnsupportedOperationException("Excel export is not implemented.");
    }
}
