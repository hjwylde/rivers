package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.ImageDocument;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static java.util.Objects.requireNonNull;

public final class MapsPresenter implements MapsContract.Presenter {
    private final MapsContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public MapsPresenter(@NonNull MapsContract.View view, @NonNull RiversApi riversApi) {
        mView = requireNonNull(view);
        mRiversApi = requireNonNull(riversApi);
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
    public void getSectionSuggestions(@NonNull String query) {
        Subscription subscription = mRiversApi.searchSections(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Section>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onGetSectionSuggestionsFailure(e);
                    }

                    @Override
                    public void onNext(List<Section> sections) {
                        List<SectionSuggestion> sectionSuggestions = new ArrayList<>();
                        for (Section section : sections) {
                            sectionSuggestions.add(new SectionSuggestion(section));
                        }

                        mView.setSectionSuggestions(sectionSuggestions);
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