package com.hjwylde.reactivex.observers;

import android.support.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;

import static java.util.Objects.requireNonNull;

public final class DisposableObserverDecorator<T> extends DisposableObserver<T> {
    private final Observer<T> mObserver;

    private DisposableObserverDecorator(@NonNull Observer<T> observer) {
        mObserver = requireNonNull(observer);
    }

    public static <T> DisposableObserver<T> decorate(Observer<T> observer) {
        return new DisposableObserverDecorator<>(observer);
    }

    @Override
    public void onComplete() {
        mObserver.onComplete();
    }

    @Override
    public void onError(Throwable t) {
        mObserver.onError(t);
    }

    @Override
    public void onNext(T t) {
        mObserver.onNext(t);
    }
}