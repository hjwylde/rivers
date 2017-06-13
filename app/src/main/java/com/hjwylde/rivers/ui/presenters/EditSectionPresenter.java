package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.db.models.ImageDocument;
import com.hjwylde.rivers.db.models.SectionDocument;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.EditSectionContract;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static java.util.Objects.requireNonNull;

public final class EditSectionPresenter implements EditSectionContract.Presenter {
    private final EditSectionContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public EditSectionPresenter(@NonNull EditSectionContract.View view, @NonNull RiversApi riversApi) {
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

    @Override
    public void updateSection(@NonNull SectionDocument.Builder builder) {
        Subscription subscription = mRiversApi.updateSection(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Section>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onUpdateSectionFailure(e);
                    }

                    @Override
                    public void onNext(Section section) {
                        mView.onUpdateSectionSuccess(section);
                    }
                });

        mSubscriptions.add(subscription);
    }
}
