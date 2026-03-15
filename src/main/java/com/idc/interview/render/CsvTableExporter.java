package com.idc.interview.render;

import com.idc.interview.model.MarketShareTable;
import java.util.Objects;

/**
 * Placeholder for a future flat-file export.
 */
public final class CsvTableExporter {
    public String export(MarketShareTable table) {
        Objects.requireNonNull(table, "table must not be null.");
        throw new UnsupportedOperationException("CSV export is not implemented.");
    }
}
