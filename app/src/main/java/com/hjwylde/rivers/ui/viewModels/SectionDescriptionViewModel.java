package com.hjwylde.rivers.ui.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class SectionDescriptionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Section> mSection = new MutableLiveData<>();

    @NonNull
    public LiveData<Section> getSection(@NonNull String id) {
        if (mSection.getValue() == null || !mSection.getValue().getId().equals(id)) {
            mDisposables.clear();

            Disposable disposable = mRepository.streamSection(id)
                    .subscribe(mSection::postValue);

            mDisposables.add(disposable);
        }

        return mSection;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }
}