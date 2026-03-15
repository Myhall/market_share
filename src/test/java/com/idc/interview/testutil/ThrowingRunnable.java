package com.idc.interview.testutil;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}
