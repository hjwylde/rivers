package com.hjwylde.rivers.ui.activities.sectionDescription;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.reactivex.observers.DisposableObserverDecorator;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public final class SectionDescriptionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    private BehaviorSubject<Section> mSectionSubject = BehaviorSubject.create();
    private Disposable mSectionDisposable = DisposableObserverDecorator.decorate(mSectionSubject);

    @NonNull
    public Observable<Section> getSection(@NonNull String id) {
        if (mSectionSubject.getValue() == null || !mSectionSubject.getValue().getId().equals(id)) {
            mSectionDisposable.dispose();

            mSectionDisposable = mRepository.streamSection(id)
                    .toObservable()
                    .subscribeWith(
                            DisposableObserverDecorator.decorate(mSectionSubject)
                    );
        }

        return mSectionSubject;
    }

    @Override
    protected void onCleared() {
        mSectionDisposable.dispose();
    }
}