package com.hjwylde.rivers.ui.presenters;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.services.Repository;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.util.SectionQuery;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static java.util.Objects.requireNonNull;

public final class MapsPresenter implements MapsContract.Presenter {
    private final MapsContract.View mView;
    private final Repository mRepository;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public MapsPresenter(@NonNull MapsContract.View view, @NonNull Repository repository) {
        mView = requireNonNull(view);
        mRepository = requireNonNull(repository);
    }

    @Override
    public void getSectionSuggestions(@NonNull String query) {
        if (query.isEmpty()) {
            mView.setSectionSuggestions(new ArrayList<>());
            return;
        }

        SectionQuery sectionQuery = new SectionQuery(query);

        Disposable disposable = mRepository.getSections()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(sectionQuery::test)
                .map(SectionSuggestion::new)
                .take(5)
                .toList()
                .subscribe(mView::setSectionSuggestions, mView::onGetSectionSuggestionsFailure);

        mDisposables.add(disposable);
    }

    @Override
    public void getSections() {
        Disposable disposable = mRepository.getSections()
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