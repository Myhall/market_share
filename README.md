# IDC Market Share Service

Java 21 service for loading quarterly market-share CSV data, transforming it into an immutable table model, querying vendor rows, sorting results, and rendering semantic HTML.

## Overview

The service is organized around a small primary API:

- `MarketShareService` loads data and exposes the main use cases.
- `MarketShareRequest` identifies the country and timescale to render.
- `MarketShareTable` provides lookup and sorting operations over the resulting table.
- `HtmlTableRenderer` renders a complete HTML document for the table.

The repository also includes:

- A sample dataset at `assignment/data.csv`
- A runnable demo entry point in `com.idc.interview.MarketShareDemo`
- Placeholder CSV and Excel exporters for future extension

## Capabilities

- Read UTF-8 CSV input with OpenCSV
- Validate required headers and row fields
- Preserve raw decimal unit values until table generation
- Normalize units to whole display values using `HALF_UP`
- Calculate market-share percentages to one decimal place
- Find a vendor and its row position
- Sort vendor rows alphabetically or by unit volume
- Render accessible HTML with a caption, header, body, and total row

## Package Layout

- `com.idc.interview`
  Main service, exception, table builder, and demo entry point
- `com.idc.interview.model`
  Request and table domain types
- `com.idc.interview.input`
  CSV ingestion and validation
- `com.idc.interview.render`
  HTML rendering, stylesheet, numeric formatting, and export placeholders

## Usage

```java
import com.idc.interview.MarketShareService;
import com.idc.interview.model.MarketShareRequest;
import com.idc.interview.model.MarketShareTable;

import java.nio.file.Path;

MarketShareService service = new MarketShareService();
MarketShareTable table = service.load(
        Path.of("assignment", "data.csv"),
        new MarketShareRequest("Czech Republic", "2010 Q4")
);

MarketShareTable sorted = service.sortByUnitsDescending(table);
String html = service.renderHtml(sorted, "table.css");
```

## Error Handling

Operational validation failures are reported as `MarketShareException`. Messages are specific enough to distinguish cases such as:

- Missing or duplicate required headers
- Missing row fields
- Invalid or negative unit values
- Duplicate vendor rows within the requested slice
- Missing data for a request
- Overflow or zero-total normalization results

## Build And Verification

```bash
./scripts/compile.sh
./scripts/test.sh
./scripts/demo.sh
```

The verification flow intentionally uses a custom Java check runner instead of JUnit. The assignment restricts the solution to Java SE and OpenCSV, so the test support under `src/test/java` stays within those boundaries and executes through `com.idc.interview.MarketShareCheckRunner`.

The demo writes:

- `output/market-share-table.html`
- `output/table.css`
