package com.hjwylde.reactivex.observers;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import static com.hjwylde.rivers.util.Preconditions.requireNull;
import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
abstract public class LifecycleBoundSingleObserver<T> implements LifecycleObserver, SingleObserver<T> {
    private Lifecycle mLifecycle;

    private Disposable mDisposable;
    private Lifecycle.State mBirth;

    public LifecycleBoundSingleObserver(@NonNull LifecycleOwner owner) {
        mLifecycle = owner.getLifecycle();
        mLifecycle.addObserver(this);
    }

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {
        requireTrue(mLifecycle.getCurrentState().isAtLeast(Lifecycle.State.INITIALIZED));
        requireNull(mDisposable);

        mDisposable = requireNonNull(disposable);
        mBirth = mLifecycle.getCurrentState();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChanged() {
        if (mDisposable == null) {
            return;
        }

        if (!mLifecycle.getCurrentState().isAtLeast(mBirth)) {
            mDisposable.dispose();

            mLifecycle.removeObserver(this);
        }
    }
}