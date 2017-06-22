package com.hjwylde.lifecycle;

import android.support.annotation.NonNull;

import static com.hjwylde.lifecycle.CompletableResult.Code.ERROR;
import static com.hjwylde.lifecycle.CompletableResult.Code.OK;
import static java.util.Objects.requireNonNull;


public class CompletableResult<T> {
    private final Code mCode;

    private T mResult;
    private Throwable mThrowable;

    private CompletableResult(@NonNull T result) {
        mCode = OK;

        mResult = requireNonNull(result);
    }

    private CompletableResult(@NonNull Throwable t) {
        mCode = ERROR;

        mThrowable = requireNonNull(t);
    }

    @NonNull
    public static <T> CompletableResult<T> error(@NonNull Throwable t) {
        return new CompletableResult(t);
    }

    @NonNull
    public static <T> CompletableResult<T> ok(T result) {
        return new CompletableResult(result);
    }

    @NonNull
    public Code code() {
        return mCode;
    }

    public T getResult() {
        return mResult;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public enum Code {
        OK, ERROR;
    }
}
