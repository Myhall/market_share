package com.idc.interview.render;

import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.TableMetadata;
import com.idc.interview.model.VendorResult;
import java.util.Objects;

/**
 * Renders the market-share table as a complete HTML document.
 */
public final class HtmlTableRenderer {
    private static final String TD_CLOSE = "</td>";

    public String render(MarketShareTable table, String stylesheetHref) {
        Objects.requireNonNull(table, "table must not be null.");
        String stylesheet = requireNonBlank(stylesheetHref, "stylesheetHref");
        String caption = buildCaption(table.metadata());

        StringBuilder html = new StringBuilder(2048);
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"UTF-8\">\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("  <title>").append(escape(caption)).append("</title>\n");
        html.append("  <link rel=\"stylesheet\" href=\"").append(escape(stylesheet)).append("\">\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <main>\n");
        html.append("    <table class=\"market-share\">\n");
        html.append("      <caption>").append(escape(caption)).append("</caption>\n");
        html.append("      <thead>\n");
        html.append("        <tr><th>Vendor</th><th>Units</th><th>Share</th></tr>\n");
        html.append("      </thead>\n");
        html.append("      <tbody>\n");
        for (VendorResult row : table.vendorRows()) {
            appendRow(html, row);
        }
        html.append("      </tbody>\n");
        html.append("      <tfoot>\n");
        appendRow(html, table.totalRow());
        html.append("      </tfoot>\n");
        html.append("    </table>\n");
        html.append("  </main>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }

    private void appendRow(StringBuilder html, VendorResult row) {
        html.append("        <tr>");
        html.append("<td>").append(escape(row.vendor())).append(TD_CLOSE);
        html.append("<td class=\"numeric\">").append(NumberFormatter.formatUnits(row.units())).append(TD_CLOSE);
        html.append("<td class=\"numeric\">").append(NumberFormatter.formatShare(row.sharePercentage())).append(TD_CLOSE);
        html.append("</tr>\n");
    }

    private String buildCaption(TableMetadata metadata) {
        return "Table %s, %s, %s, %s".formatted(
                metadata.tableNumber(),
                metadata.title(),
                toCaptionCountry(metadata.country()),
                metadata.timescaleLabel()
        );
    }

    private String toCaptionCountry(String country) {
        if ("Czech Republic".equals(country)) {
            return "the Czech Republic";
        }
        return country;
    }

    private String escape(String value) {
        Objects.requireNonNull(value, "value must not be null.");

        StringBuilder escaped = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            switch (current) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(current);
            }
        }
        return escaped.toString();
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value.trim();
    }
}
