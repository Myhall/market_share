package com.idc.interview.input;

import com.idc.interview.MarketShareException;
import com.idc.interview.model.RawMarketRecord;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Reads validated market-share input rows from CSV.
 */
public final class MarketShareCsvReader {
    private static final Logger LOGGER = Logger.getLogger(MarketShareCsvReader.class.getName());
    private static final String COUNTRY = "Country";
    private static final String TIMESCALE = "Timescale";
    private static final String VENDOR = "Vendor";
    private static final String UNITS = "Units";

    public List<RawMarketRecord> read(Path csvPath) {
        Objects.requireNonNull(csvPath, "csvPath must not be null.");
        LOGGER.info(() -> "Loading CSV file from " + csvPath.toAbsolutePath());

        try (Reader fileReader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(fileReader).build()) {
            HeaderIndexes headerIndexes = readHeader(csvReader.readNext());
            List<RawMarketRecord> records = new ArrayList<>();
            String[] row;
            int lineNumber = 1;
            while ((row = csvReader.readNext()) != null) {
                lineNumber++;
                if (isBlankRow(row)) {
                    continue;
                }
                records.add(parseRecord(lineNumber, row, headerIndexes));
            }
            LOGGER.info(() -> "Parsed " + records.size() + " raw records");
            return List.copyOf(records);
        } catch (IOException | CsvValidationException exception) {
            throw new MarketShareException("Failed to read CSV file: " + csvPath.toAbsolutePath(), exception);
        }
    }

    private HeaderIndexes readHeader(String[] headers) {
        if (headers == null || headers.length == 0) {
            throw new MarketShareException("The input CSV does not contain a header row.");
        }

        Map<String, Integer> indexByHeader = new LinkedHashMap<>();
        for (int index = 0; index < headers.length; index++) {
            String header = normalizeHeader(headers[index], index == 0);
            if (header.isEmpty()) {
                continue;
            }

            Integer previous = indexByHeader.putIfAbsent(header, index);
            if (previous != null && isRequiredHeader(header)) {
                throw new MarketShareException("Duplicate required header: " + header);
            }
        }

        return new HeaderIndexes(
                findRequiredIndex(indexByHeader, COUNTRY),
                findRequiredIndex(indexByHeader, TIMESCALE),
                findRequiredIndex(indexByHeader, VENDOR),
                findRequiredIndex(indexByHeader, UNITS)
        );
    }

    private RawMarketRecord parseRecord(int lineNumber, String[] row, HeaderIndexes headerIndexes) {
        String country = requireField(readCell(row, headerIndexes.countryIndex()), COUNTRY, lineNumber);
        String timescale = requireField(readCell(row, headerIndexes.timescaleIndex()), TIMESCALE, lineNumber);
        String vendor = requireField(readCell(row, headerIndexes.vendorIndex()), VENDOR, lineNumber);
        String unitsText = requireField(readCell(row, headerIndexes.unitsIndex()), UNITS, lineNumber);

        BigDecimal units;
        try {
            units = new BigDecimal(unitsText);
        } catch (NumberFormatException exception) {
            throw new MarketShareException("Line " + lineNumber + " contains an invalid Units value: " + unitsText, exception);
        }

        if (units.signum() < 0) {
            throw new MarketShareException("Line " + lineNumber + " contains negative Units: " + unitsText);
        }

        return new RawMarketRecord(country, timescale, vendor, units);
    }

    private int findRequiredIndex(Map<String, Integer> indexByHeader, String requiredHeader) {
        Integer index = indexByHeader.get(requiredHeader);
        if (index == null) {
            throw new MarketShareException("Missing required header: " + requiredHeader);
        }
        return index;
    }

    private String normalizeHeader(String header, boolean firstHeader) {
        String value = Objects.toString(header, "").trim();
        if (firstHeader && value.startsWith("\uFEFF")) {
            return value.substring(1).trim();
        }
        return value;
    }

    private boolean isRequiredHeader(String header) {
        return COUNTRY.equals(header) || TIMESCALE.equals(header) || VENDOR.equals(header) || UNITS.equals(header);
    }

    private boolean isBlankRow(String[] row) {
        for (String cell : row) {
            if (cell != null && !cell.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String requireField(String value, String fieldName, int lineNumber) {
        if (value == null || value.isBlank()) {
            throw new MarketShareException("Line " + lineNumber + " is missing required field: " + fieldName);
        }
        return value.trim();
    }

    private String readCell(String[] row, int index) {
        if (index < 0 || index >= row.length) {
            return "";
        }
        return row[index];
    }

    private record HeaderIndexes(int countryIndex, int timescaleIndex, int vendorIndex, int unitsIndex) {
    }
}
