package com.idc.interview.testutil;

import java.math.BigDecimal;
import java.util.Objects;

public final class TestSupport {
    private TestSupport() {
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            fail(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            fail(message + " Expected: " + expected + ", actual: " + actual);
        }
    }

    public static void assertContains(String text, String expectedFragment, String message) {
        if (text == null || !text.contains(expectedFragment)) {
            fail(message + " Missing fragment: " + expectedFragment);
        }
    }

    public static void assertBigDecimalEquals(String expected, BigDecimal actual, String message) {
        BigDecimal expectedValue = new BigDecimal(expected);
        if (actual == null || expectedValue.compareTo(actual) != 0) {
            fail(message + " Expected: " + expectedValue + ", actual: " + actual);
        }
    }

    public static <T extends Exception> T assertThrows(Class<T> expectedType, ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Exception exception) {
            if (expectedType.isInstance(exception)) {
                return expectedType.cast(exception);
            }
            fail(message + " Expected exception " + expectedType.getSimpleName() + ", but got " + exception.getClass().getSimpleName());
        }

        fail(message + " Expected exception " + expectedType.getSimpleName() + ", but nothing was thrown.");
        return null;
    }

    public static void assertMessageContains(Throwable throwable, String expectedFragment, String message) {
        if (throwable == null || throwable.getMessage() == null || !throwable.getMessage().contains(expectedFragment)) {
            fail(message + " Missing fragment: " + expectedFragment);
        }
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }
}
