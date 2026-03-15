package com.idc.interview;

import com.idc.interview.input.MarketShareCsvReader;
import com.idc.interview.model.RawMarketRecord;
import com.idc.interview.testutil.TestSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MarketShareCsvReaderChecks {
    private MarketShareCsvReaderChecks() {
    }

    public static void runAll() throws Exception {
        readsValidFileAndIgnoresBlankRows();
        toleratesUtf8BomInFirstHeader();
        rejectsMissingRequiredHeader();
        rejectsNegativeUnits();
        rejectsDuplicateRequiredHeaders();
    }

    private static void readsValidFileAndIgnoresBlankRows() throws IOException {
        Path csv = createTempCsv("""
                Country,Timescale,Vendor,Units
                Czech Republic,2010 Q4,"ACME, Inc.",42.4

                Czech Republic,2010 Q4,Dell,10
                """);

        List<RawMarketRecord> records = new MarketShareCsvReader().read(csv);

        TestSupport.assertEquals(2L, records.size(), "Blank rows should be ignored.");
        TestSupport.assertEquals("ACME, Inc.", records.getFirst().vendor(), "Quoted values should be parsed through OpenCSV.");
        TestSupport.assertBigDecimalEquals("42.4", records.getFirst().units(), "Units should preserve decimal precision.");
    }

    private static void toleratesUtf8BomInFirstHeader() throws IOException {
        Path csv = createTempCsv("""
                \uFEFFCountry,Timescale,Vendor,Units
                Czech Republic,2010 Q4,Dell,11455.09902
                """);

        List<RawMarketRecord> records = new MarketShareCsvReader().read(csv);

        TestSupport.assertEquals(1L, records.size(), "BOM-prefixed headers should still be accepted.");
        TestSupport.assertEquals(new BigDecimal("11455.09902"), records.getFirst().units(), "Units should parse from BOM-prefixed files.");
    }

    private static void rejectsMissingRequiredHeader() throws IOException {
        Path csv = createTempCsv("""
                Country,Timescale,Vendor
                Czech Republic,2010 Q4,Dell
                """);

        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareCsvReader().read(csv),
                "Missing Units header should fail fast."
        );
        TestSupport.assertMessageContains(exception, "Missing required header: Units", "Header failures should be identified by message.");
    }

    private static void rejectsNegativeUnits() throws IOException {
        Path csv = createTempCsv("""
                Country,Timescale,Vendor,Units
                Czech Republic,2010 Q4,Dell,-1
                """);

        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareCsvReader().read(csv),
                "Negative unit values must be rejected."
        );
        TestSupport.assertMessageContains(exception, "contains negative Units", "Negative-unit failures should be identified by message.");
    }

    private static void rejectsDuplicateRequiredHeaders() throws IOException {
        Path csv = createTempCsv("""
                Country,Timescale,Vendor,Units,Units
                Czech Republic,2010 Q4,Dell,1,2
                """);

        MarketShareException exception = TestSupport.assertThrows(
                MarketShareException.class,
                () -> new MarketShareCsvReader().read(csv),
                "Duplicate required headers should fail fast."
        );
        TestSupport.assertMessageContains(exception, "Duplicate required header: Units", "Duplicate-header failures should be identified by message.");
    }

    private static Path createTempCsv(String content) throws IOException {
        Path file = Files.createTempFile("idc-market-share-reader-", ".csv");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
