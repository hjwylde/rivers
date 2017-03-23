package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.CreateSectionContract;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class CreateSectionPresenter implements CreateSectionContract.Presenter {
    @SuppressWarnings("unused")
    private final CreateSectionContract.View mView;
    @SuppressWarnings("unused")
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public CreateSectionPresenter(@NonNull CreateSectionContract.View view, @NonNull RiversApi riversApi) {
        mView = checkNotNull(view);
        mRiversApi = checkNotNull(riversApi);
    }

    @Override
    public void createImage(@NonNull Image image) {
        // TODO (hjw)
    }

    @Override
    public void createSection(@NonNull Section.Builder builder) {
        Subscription subscription = mRiversApi.createSection(builder)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Section>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onCreateSectionFailure(e);
                    }

                    @Override
                    public void onNext(Section section) {
                        mView.onCreateSectionSuccess(section);
                    }
                });

        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
