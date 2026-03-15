package com.idc.interview;

import com.idc.interview.testutil.ThrowingRunnable;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MarketShareCheckRunner {
    private static final Logger LOGGER = Logger.getLogger(MarketShareCheckRunner.class.getName());

    private MarketShareCheckRunner() {
    }

    public static void main(String[] args) {
        List<NamedCheck> checks = List.of(
                new NamedCheck("MarketShareCsvReaderChecks", MarketShareCsvReaderChecks::runAll),
                new NamedCheck("MarketShareTableBuilderChecks", MarketShareTableBuilderChecks::runAll),
                new NamedCheck("HtmlTableRendererChecks", HtmlTableRendererChecks::runAll),
                new NamedCheck("MarketShareServiceAcceptanceChecks", MarketShareServiceAcceptanceChecks::runAll)
        );

        int passed = 0;
        for (NamedCheck check : checks) {
            try {
                check.body().run();
                LOGGER.info(() -> "[PASS] " + check.name());
                passed++;
            } catch (RuntimeException exception) {
                LOGGER.log(Level.SEVERE, exception, () -> "[FAIL] " + check.name());
                throw exception;
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, exception, () -> "[FAIL] " + check.name());
                throw new IllegalStateException("Check execution failed for " + check.name(), exception);
            }
        }

        int passedChecks = passed;
        LOGGER.info(() -> "Executed " + passedChecks + " check groups successfully.");
    }

    private record NamedCheck(String name, ThrowingRunnable body) {
    }
}
