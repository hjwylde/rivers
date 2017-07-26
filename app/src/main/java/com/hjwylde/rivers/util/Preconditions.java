package com.hjwylde.rivers.util;

import android.os.Looper;

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

    public static void requireWorkerThread() {
        requireTrue(Looper.myLooper() != Looper.getMainLooper());
    }
}
