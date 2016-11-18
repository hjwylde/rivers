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

    public MapsPresenter(@NonNull MapsContract.View mView, @NonNull RiversApi mRiversApi) {
        this.mView = checkNotNull(mView);
        this.mRiversApi = checkNotNull(mRiversApi);
    }

    @Override
    public void getSections() {
        Subscription subscription = mRiversApi.getSections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Section>>() {
                    @Override
                    public void onCompleted() {
                        mView.refreshMap();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onGetSectionsFailure(e);
                    }

                    @Override
                    public void onNext(List<Section> sections) {
                        mView.setSections(sections);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
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
}
