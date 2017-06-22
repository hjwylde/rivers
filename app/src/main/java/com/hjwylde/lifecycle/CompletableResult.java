package com.hjwylde.lifecycle;

import android.support.annotation.NonNull;

import static com.hjwylde.lifecycle.CompletableResult.Code.ERROR;
import static com.hjwylde.lifecycle.CompletableResult.Code.OK;
import static java.util.Objects.requireNonNull;


public class CompletableResult {
    private final Code mCode;

    private Throwable mThrowable;

    private CompletableResult() {
        mCode = OK;
    }

    private CompletableResult(@NonNull Throwable t) {
        mCode = ERROR;

        mThrowable = requireNonNull(t);
    }

    @NonNull
    public static CompletableResult error(@NonNull Throwable t) {
        return new CompletableResult(t);
    }

    @NonNull
    public static CompletableResult ok() {
        return new CompletableResult();
    }

    @NonNull
    public Code code() {
        return mCode;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public enum Code {
        OK, ERROR;
    }
}
