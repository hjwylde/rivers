package com.hjwylde.rivers.util;

public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }

        return obj;
    }
}
