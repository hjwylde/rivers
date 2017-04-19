package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.MapsContract;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class MapsPresenter implements MapsContract.Presenter {
    private final MapsContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public MapsPresenter(@NonNull MapsContract.View view, @NonNull RiversApi riversApi) {
        mView = checkNotNull(view);
        mRiversApi = checkNotNull(riversApi);
    }

    @Override
    public void deleteSection(@NonNull Section section) {
        Subscription subscription = mRiversApi.deleteSection(section)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        mView.onDeleteSectionSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDeleteSectionFailure(e);
                    }

                    @Override
                    public void onNext(Void section) {
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
    public void streamSections() {
        Subscription subscription = mRiversApi.streamSections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Section>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Section> sections) {
                        mView.setSections(sections);
                        mView.refreshMap();
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}