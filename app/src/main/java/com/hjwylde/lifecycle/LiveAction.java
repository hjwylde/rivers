package com.hjwylde.lifecycle;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

public class LiveAction<T> extends LiveData<CompletableResult<T>> {
    protected void postComplete(T result) {
        postValue(CompletableResult.ok(result));
    }

    protected void postError(@NonNull Throwable t) {
        postValue(CompletableResult.error(t));
    }
}