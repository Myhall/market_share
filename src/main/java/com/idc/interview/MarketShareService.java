package com.idc.interview;

import com.idc.interview.input.MarketShareCsvReader;
import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.VendorResult;
import com.idc.interview.render.HtmlTableRenderer;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Small public facade for loading, querying, sorting, and rendering tables.
 */
public final class MarketShareService {
    private static final String TABLE_MUST_NOT_BE_NULL = "table must not be null.";

    private final MarketShareCsvReader csvReader;
    private final MarketShareTableBuilder tableBuilder;
    private final HtmlTableRenderer htmlRenderer;

    public MarketShareService() {
        this(new MarketShareCsvReader(), new MarketShareTableBuilder(), new HtmlTableRenderer());
    }

    MarketShareService(MarketShareCsvReader csvReader, MarketShareTableBuilder tableBuilder, HtmlTableRenderer htmlRenderer) {
        this.csvReader = Objects.requireNonNull(csvReader, "csvReader must not be null.");
        this.tableBuilder = Objects.requireNonNull(tableBuilder, "tableBuilder must not be null.");
        this.htmlRenderer = Objects.requireNonNull(htmlRenderer, "htmlRenderer must not be null.");
    }

    public MarketShareTable load(Path csvPath, MarketShareRequest request) {
        return tableBuilder.build(csvReader.read(csvPath), request);
    }

    public Optional<VendorResult> findVendor(MarketShareTable table, String vendor) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return table.findVendor(vendor);
    }

    public OptionalInt findVendorRow(MarketShareTable table, String vendor) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return table.findVendorRow(vendor);
    }

    public MarketShareTable sortByVendor(MarketShareTable table) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return table.sortedByVendor();
    }

    public MarketShareTable sortByUnitsAscending(MarketShareTable table) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return table.sortedByUnitsAscending();
    }

    public MarketShareTable sortByUnitsDescending(MarketShareTable table) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return table.sortedByUnitsDescending();
    }

    public String renderHtml(MarketShareTable table, String stylesheetHref) {
        Objects.requireNonNull(table, TABLE_MUST_NOT_BE_NULL);
        return htmlRenderer.render(table, stylesheetHref);
    }
}
