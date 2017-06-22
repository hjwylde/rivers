package com.hjwylde.lifecycle;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

public class LiveAction extends LiveData<CompletableResult> {
    protected void postComplete(Object... unused) {
        postValue(CompletableResult.ok());
    }

    protected void postError(@NonNull Throwable t) {
        postValue(CompletableResult.error(t));
    }
}