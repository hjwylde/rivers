package com.hjwylde.rivers.util;

public final class Preconditions {
    private Preconditions() {
    }

    public static void requireTrue(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }
}
