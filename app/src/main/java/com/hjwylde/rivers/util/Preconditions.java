package com.hjwylde.rivers.util;

public final class Preconditions {
    private Preconditions() {
    }

    public static void requireNull(Object obj) {
        if (obj != null) {
            throw new IllegalStateException();
        }
    }

    public static void requireTrue(boolean condition) {
        if (!condition) {
            throw new IllegalStateException();
        }
    }
}
