package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static java.util.Objects.requireNonNull;

public final class MapsPresenter implements MapsContract.Presenter {
    private final MapsContract.View mView;
    private final RiversApi mRiversApi;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public MapsPresenter(@NonNull MapsContract.View view, @NonNull RiversApi riversApi) {
        mView = requireNonNull(view);
        mRiversApi = requireNonNull(riversApi);
    }

    @Override
    public void deleteSection(@NonNull Section section) {
        Disposable disposable = mRiversApi.deleteSection(section)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onDeleteSectionSuccess, mView::onDeleteSectionFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getImage(@NonNull String id) {
        Disposable disposable = mRiversApi.getImage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> {
                    mView.setImage(image);
                    mView.refreshImage();
                }, mView::onGetImageFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getSection(@NonNull String id) {
        Disposable disposable = mRiversApi.getSection(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mView::onGetSectionSuccess, mView::onGetSectionFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getSectionSuggestions(@NonNull String query) {
        Disposable disposable = mRiversApi.findSection(query)
                .observeOn(AndroidSchedulers.mainThread())
                .map(SectionSuggestion::new)
                .take(5)
                .toList()
                .subscribe(mView::setSectionSuggestions, mView::onGetSectionSuggestionsFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getSections() {
        Disposable disposable = mRiversApi.getSections()
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(sections -> {
                    mView.setSections(sections);
                    mView.refreshMap();
                });

        mDisposables.add(disposable);
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}