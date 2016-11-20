package com.hjwylde.rivers.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Preconditions {
    private Preconditions() {
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }

        return obj;
    }
}
