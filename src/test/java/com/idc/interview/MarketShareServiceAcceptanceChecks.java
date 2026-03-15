package com.idc.interview;

import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.VendorResult;
import com.idc.interview.testutil.TestSupport;

import java.nio.file.Path;
import java.util.List;

public final class MarketShareServiceAcceptanceChecks {
    private MarketShareServiceAcceptanceChecks() {
    }

    public static void runAll() {
        loadsProvidedDataAndSupportsRequiredOperations();
    }

    private static void loadsProvidedDataAndSupportsRequiredOperations() {
        MarketShareService service = new MarketShareService();
        MarketShareTable table = service.load(Path.of("assignment", "data.csv"), new MarketShareRequest("Czech Republic", "2010 Q4"));

        TestSupport.assertEquals(7L, table.vendorRows().size(), "The provided CSV slice should create seven vendor rows.");
        TestSupport.assertEquals(22414L, table.totalRow().units(), "The total row should equal the sum of normalized vendor units.");

        VendorResult dell = service.findVendor(table, "Dell")
                .orElseThrow(() -> new AssertionError("Dell should exist in the provided dataset."));
        VendorResult apple = service.findVendor(table, "Apple")
                .orElseThrow(() -> new AssertionError("Apple should exist in the provided dataset."));

        TestSupport.assertEquals(11455L, dell.units(), "Dell units should match the normalized dataset.");
        TestSupport.assertBigDecimalEquals("51.1", dell.sharePercentage(), "Dell share should match the normalized dataset.");
        TestSupport.assertEquals(267L, apple.units(), "Apple units should match the normalized dataset.");
        TestSupport.assertBigDecimalEquals("1.2", apple.sharePercentage(), "Apple share should match the normalized dataset.");

        TestSupport.assertEquals(2L, service.findVendorRow(table, "Dell").orElseThrow(), "Original row order should match CSV order.");

        MarketShareTable alphabetical = service.sortByVendor(table);
        List<String> alphabeticVendors = alphabetical.vendorRows().stream().map(VendorResult::vendor).toList();
        TestSupport.assertEquals(
                List.of("Acer", "Apple", "ASUS", "Dell", "Fujitsu Siemens", "Hewlett-Packard", "Lenovo"),
                alphabeticVendors,
                "Alphabetical sorting should order vendors by name."
        );
        TestSupport.assertEquals(4L, service.findVendorRow(alphabetical, "Dell").orElseThrow(), "Dell should move to the fourth alphabetical row.");

        MarketShareTable byUnitsDescending = service.sortByUnitsDescending(table);
        List<String> descendingVendors = byUnitsDescending.vendorRows().stream().map(VendorResult::vendor).toList();
        TestSupport.assertEquals(
                List.of("Dell", "Hewlett-Packard", "Fujitsu Siemens", "Acer", "Lenovo", "ASUS", "Apple"),
                descendingVendors,
                "Unit sorting should order vendors from highest to lowest units."
        );
        TestSupport.assertEquals(1L, service.findVendorRow(byUnitsDescending, "Dell").orElseThrow(), "Dell should be first after descending unit sort.");

        String html = service.renderHtml(byUnitsDescending, "table.css");
        TestSupport.assertContains(html, "Table 1, PC Quarterly Market Share, the Czech Republic, 4Q10", "HTML export should include the requested caption.");
        TestSupport.assertContains(html, "11,455", "HTML export should format normalized units with grouping separators.");
    }
}
