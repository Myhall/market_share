package com.idc.interview.render;

/**
 * Default stylesheet written by the demo application.
 */
public final class MarketShareStylesheet {
    public static final String TABLE_CSS = """
            * {
              box-sizing: border-box;
            }

            body {
              margin: 0;
              padding: 20px 16px;
              background: #ffffff;
              color: #000000;
              font-family: "Times New Roman", Times, serif;
            }

            main {
              width: min(720px, 100%);
              margin: 0 auto;
            }

            table.market-share {
              width: 100%;
              border-collapse: collapse;
              table-layout: fixed;
              background: #dddddd;
              border: 1px solid #1c1c1c;
            }

            table.market-share caption {
              caption-side: top;
              text-align: left;
              padding: 0 0 2px 38px;
              color: #000000;
              font-size: 1rem;
              font-weight: 400;
            }

            table.market-share th,
            table.market-share td {
              padding: 2px 10px;
              border: 1px solid #1c1c1c;
              background: #dddddd;
              text-align: center;
              font-size: 1rem;
              font-weight: 400;
            }

            table.market-share thead th {
              background: #d7d7d7;
            }

            table.market-share td.numeric {
              text-align: center;
              font-variant-numeric: tabular-nums;
            }

            table.market-share tfoot td {
              background: #e6e58f;
            }
            """;

    private MarketShareStylesheet() {
    }
}
