package com.idc.interview;

import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.VendorResult;
import com.idc.interview.render.MarketShareStylesheet;
import com.idc.interview.render.NumberFormatter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Demonstrates the public API against the provided CSV file.
 */
public final class MarketShareDemo {
    private static final Logger LOGGER = Logger.getLogger(MarketShareDemo.class.getName());

    private MarketShareDemo() {
    }

    public static void main(String[] args) throws IOException {
        MarketShareService service = new MarketShareService();
        MarketShareRequest request = new MarketShareRequest("Czech Republic", "2010 Q4");

        MarketShareTable table = service.load(Path.of("assignment", "data.csv"), request);
        VendorResult dell = service.findVendor(table, "Dell")
                .orElseThrow(() -> new IllegalStateException("Dell should exist in the demo table."));
        int originalRow = service.findVendorRow(table, "Dell")
                .orElseThrow(() -> new IllegalStateException("Dell row should exist in the demo table."));

        MarketShareTable sorted = service.sortByUnitsDescending(table);

        Path outputDirectory = Path.of("output");
        Files.createDirectories(outputDirectory);
        Path htmlPath = outputDirectory.resolve("market-share-table.html");
        Path cssPath = outputDirectory.resolve("table.css");

        Files.writeString(htmlPath, service.renderHtml(sorted, cssPath.getFileName().toString()), StandardCharsets.UTF_8);
        Files.writeString(cssPath, MarketShareStylesheet.TABLE_CSS, StandardCharsets.UTF_8);

        LOGGER.info(() -> "Dell units: " + NumberFormatter.formatUnits(dell.units()));
        LOGGER.info(() -> "Dell share: " + NumberFormatter.formatShare(dell.sharePercentage()));
        LOGGER.info(() -> "Dell row in original order: " + originalRow);
        LOGGER.info(() -> "HTML exported to " + htmlPath.toAbsolutePath());
        LOGGER.info(() -> "CSS exported to " + cssPath.toAbsolutePath());
    }
}
