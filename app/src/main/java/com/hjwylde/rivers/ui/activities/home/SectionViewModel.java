package com.hjwylde.rivers.ui.activities.home;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.reactivex.observers.DisposableObserverDecorator;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public final class SectionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    private BehaviorSubject<Section> mSectionSubject = BehaviorSubject.create();
    private Disposable mSectionDisposable = DisposableObserverDecorator.decorate(mSectionSubject);

    @NonNull
    public Completable deleteSection(@NonNull Section section) {
        return mRepository.deleteSection(section)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Maybe<Image> getImage(@NonNull String id) {
        return mRepository.getImage(id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Observable<Section> streamSection(@NonNull String id) {
        if (mSectionSubject.getValue() == null || !id.equals(mSectionSubject.getValue().getId())) {
            mSectionDisposable.dispose();

            mSectionDisposable = mRepository.streamSection(id)
                    .toObservable()
                    .subscribeWith(
                            DisposableObserverDecorator.decorate(mSectionSubject)
                    );
        }

        return mSectionSubject;
    }

    @NonNull
    public Observable<Section> streamSection() {
        return mSectionSubject;
    }

    @Override
    protected void onCleared() {
        mSectionDisposable.dispose();
    }
}