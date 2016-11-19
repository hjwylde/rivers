package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;

import rx.subscriptions.CompositeSubscription;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class CreateSectionPresenter implements CreateSectionContract.Presenter {
    private final CreateSectionContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public CreateSectionPresenter(@NonNull CreateSectionContract.View view, @NonNull RiversApi riversApi) {
        mView = checkNotNull(view);
        mRiversApi = checkNotNull(riversApi);
    }

    @Override
    public void createSection() {
        // TODO (#61)
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
