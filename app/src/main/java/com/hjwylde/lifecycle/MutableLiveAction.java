package com.hjwylde.lifecycle;

import android.support.annotation.NonNull;

public final class MutableLiveAction<T> extends LiveAction<T> {
    @Override
    public void postComplete(T result) {
        super.postComplete(result);
    }

    @Override
    public void postError(@NonNull Throwable t) {
        super.postError(t);
    }
}