package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.SectionContract;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SectionPresenter implements SectionContract.Presenter {
    private final SectionContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public SectionPresenter(@NonNull SectionContract.View view, @NonNull RiversApi riversApi) {
        mView = checkNotNull(view);
        mRiversApi = checkNotNull(riversApi);
    }

    @Override
    public void deleteSection(@NonNull Action action) {
        Subscription subscription = mRiversApi.postAction(action)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Action>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDeleteSectionFailure(e);
                    }

                    @Override
                    public void onNext(Action action) {
                        mView.onDeleteSectionSuccess(action);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void getImage(@NonNull String id) {
        Subscription subscription = mRiversApi.getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Image>() {
                    @Override
                    public void onCompleted() {
                        mView.refreshImage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onGetImageFailure(e);
                    }

                    @Override
                    public void onNext(Image image) {
                        mView.setImage(image);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
