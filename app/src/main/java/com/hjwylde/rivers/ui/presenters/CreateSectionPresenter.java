package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CreateSectionPresenter implements CreateSectionContract.Presenter {
    private final CreateSectionContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public CreateSectionPresenter(@NonNull CreateSectionContract.View view, @NonNull RiversApi riversApi) {
        mView = checkNotNull(view);
        mRiversApi = checkNotNull(riversApi);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void createSection(@NonNull Action action) {
        Subscription subscription = mRiversApi.postAction(action)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Action>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onCreateSectionFailure(e);
                    }

                    @Override
                    public void onNext(Action action) {
                        mView.onCreateSectionSuccess(action);
                    }
                });

        mSubscriptions.add(subscription);
    }
}
