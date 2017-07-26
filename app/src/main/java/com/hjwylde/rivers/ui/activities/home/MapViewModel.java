package com.hjwylde.rivers.ui.activities.home;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.reactivex.observers.DisposableObserverDecorator;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public final class MapViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    private BehaviorSubject<List<Section>> mSectionsSubject = BehaviorSubject.create();
    private Disposable mSectionsDisposable = DisposableObserverDecorator.decorate(mSectionsSubject);

    @NonNull
    public Observable<List<Section>> streamSections() {
        if (mSectionsSubject.getValue() == null) {
            mSectionsDisposable.dispose();

            mSectionsDisposable = mRepository.streamSections()
                    .toObservable()
                    .subscribeWith(
                            DisposableObserverDecorator.decorate(mSectionsSubject)
                    );
        }

        return mSectionsSubject;
    }

    @Override
    protected void onCleared() {
        mSectionsDisposable.dispose();
    }
}