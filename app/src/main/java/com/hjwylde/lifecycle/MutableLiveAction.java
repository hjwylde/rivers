package com.hjwylde.lifecycle;

import android.support.annotation.NonNull;

public final class MutableLiveAction extends LiveAction {
    @Override
    public void postComplete(Object... unused) {
        super.postComplete(unused);
    }

    @Override
    public void postError(@NonNull Throwable t) {
        super.postError(t);
    }
}