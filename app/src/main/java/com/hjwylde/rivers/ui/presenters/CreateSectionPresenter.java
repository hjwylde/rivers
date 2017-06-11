package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.ImageDocument;
import com.hjwylde.rivers.models.SectionDocument;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static java.util.Objects.requireNonNull;

public final class CreateSectionPresenter implements CreateSectionContract.Presenter {
    private final CreateSectionContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public CreateSectionPresenter(@NonNull CreateSectionContract.View view, @NonNull RiversApi riversApi) {
        mView = requireNonNull(view);
        mRiversApi = requireNonNull(riversApi);
    }

    @Override
    public void createImage(@NonNull ImageDocument.Builder builder) {
        Subscription subscription = mRiversApi.createImage(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageDocument>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onCreateImageFailure(e);
                    }

                    @Override
                    public void onNext(ImageDocument image) {
                        mView.onCreateImageSuccess(image);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void createSection(@NonNull SectionDocument.Builder builder) {
        Subscription subscription = mRiversApi.createSection(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SectionDocument>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onCreateSectionFailure(e);
                    }

                    @Override
                    public void onNext(SectionDocument section) {
                        mView.onCreateSectionSuccess(section);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void getImage(@NonNull String id) {
        Subscription subscription = mRiversApi.getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageDocument>() {
                    @Override
                    public void onCompleted() {
                        mView.refreshImage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onGetImageFailure(e);
                    }

                    @Override
                    public void onNext(ImageDocument image) {
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
