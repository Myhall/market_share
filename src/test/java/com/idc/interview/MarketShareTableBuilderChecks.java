package com.idc.interview;

import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.RawMarketRecord;
import com.idc.interview.testutil.TestSupport;

import java.math.BigDecimal;
import java.util.List;

public final class MarketShareTableBuilderChecks {
    private MarketShareTableBuilderChecks() {
    }

    public static void runAll() {
        createsTableAndCalculatesShares();
        rejectsDuplicateVendorsWithinSelectedSlice();
        rejectsMissingSlice();
        rejectsZeroTotalAfterNormalization();
    }

    private static void createsTableAndCalculatesShares() {
        MarketShareTable table = new MarketShareTableBuilder().build(
                List.of(
                        rawMarketRecord("Czech Republic", "2010 Q4", "Apple", "10.4"),
                        rawMarketRecord("Czech Republic", "2010 Q4", "Dell", "20.5"),
                        rawMarketRecord("Slovakia", "2010 Q4", "Dell", "999.9")
                ),
                new MarketShareRequest("Czech Republic", "2010 Q4")
        );

        TestSupport.assertEquals(2L, table.vendorRows().size(), "Only matching rows should be included.");
        TestSupport.assertEquals("4Q10", table.metadata().timescaleLabel(), "Timescale should be rendered in sample-table style.");
        TestSupport.assertEquals(31L, table.totalRow().units(), "Normalized vendor units should determine the total row.");
        TestSupport.assertBigDecimalEquals("32.3", table.vendorRows().getFirst().sharePercentage(), "Share percentages should be derived from normalized units.");
        TestSupport.assertBigDecimalEquals("67.7", table.vendorRows().get(1).sharePercentage(), "Share percentages should be rounded to one decimal place.");
    }

    private static void rejectsDuplicateVendorsWithinSelectedSlice() {
        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareTableBuilder().build(
                        List.of(
                                rawMarketRecord("Czech Republic", "2010 Q4", "Dell", "10"),
                                rawMarketRecord("Czech Republic", "2010 Q4", "Dell", "20")
                        ),
                        new MarketShareRequest("Czech Republic", "2010 Q4")
                ),
                "Duplicate vendor rows should be rejected."
        );
        TestSupport.assertMessageContains(exception, "Duplicate vendor entry detected", "Duplicate-vendor failures should be identified by message.");
    }

    private static void rejectsMissingSlice() {
        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareTableBuilder().build(
                        List.of(rawMarketRecord("Slovakia", "2010 Q4", "Dell", "10")),
                        new MarketShareRequest("Czech Republic", "2010 Q4")
                ),
                "A request with no matching rows should fail."
        );
        TestSupport.assertMessageContains(exception, "No data found", "Missing-slice failures should be identified by message.");
    }

    private static void rejectsZeroTotalAfterNormalization() {
        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareTableBuilder().build(
                        List.of(
                                rawMarketRecord("Czech Republic", "2010 Q4", "Apple", "0.4"),
                                rawMarketRecord("Czech Republic", "2010 Q4", "Dell", "0.4")
                        ),
                        new MarketShareRequest("Czech Republic", "2010 Q4")
                ),
                "Tables whose normalized units sum to zero should fail."
        );
        TestSupport.assertMessageContains(exception, "No positive unit data remains", "Zero-total failures should be identified by message.");
    }

    private static RawMarketRecord rawMarketRecord(String country, String timescale, String vendor, String units) {
        return new RawMarketRecord(country, timescale, vendor, new BigDecimal(units));
    }
}
