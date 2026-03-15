package com.idc.interview;

import com.idc.interview.model.MarketShareTable;
import com.idc.interview.model.TableMetadata;
import com.idc.interview.model.VendorResult;
import com.idc.interview.render.HtmlTableRenderer;
import com.idc.interview.testutil.TestSupport;

import java.math.BigDecimal;
import java.util.List;

public final class HtmlTableRendererChecks {
    private HtmlTableRendererChecks() {
    }

    public static void runAll() {
        exportsSemanticHtmlWithEscapedContent();
    }

    private static void exportsSemanticHtmlWithEscapedContent() {
        MarketShareTable table = new MarketShareTable(
                new TableMetadata("1", "PC Quarterly Market Share", "the Czech Republic", "4Q10"),
                List.of(new VendorResult("Dell & Sons <North>", 12030, new BigDecimal("10.6"))),
                new VendorResult("Total", 12030, BigDecimal.valueOf(100))
        );

        String html = new HtmlTableRenderer().render(table, "table.css");

        TestSupport.assertContains(html, "<link rel=\"stylesheet\" href=\"table.css\">", "HTML should reference the external stylesheet.");
        TestSupport.assertContains(html, "<caption>Table 1, PC Quarterly Market Share, the Czech Republic, 4Q10</caption>", "HTML should render the sample-style caption.");
        TestSupport.assertContains(html, "Dell &amp; Sons &lt;North&gt;", "Vendor names should be HTML escaped.");
        TestSupport.assertContains(html, "<tfoot>", "Total row should live in the table footer.");
        TestSupport.assertContains(html, "100%</td>", "Total row share should format as 100%.");
    }
}
