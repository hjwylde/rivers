package com.hjwylde.rivers.ui.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class CreateSectionViewModel extends ViewModel {
    // TODO (hjw): inject this
    private final Repository mRepository = RiversApplication.getRepository();
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveData<Image> mImage = new MutableLiveData<>();

    @NonNull
    public LiveData<Image> getImage() {
        return mImage;
    }

    @NonNull
    public LiveData<Image> getImage(@NonNull String id) {
        if (mImage.getValue() == null || !mImage.getValue().getId().equals(id)) {
            Disposable disposable = mRepository.getImage(id)
                    .subscribe(mImage::postValue);

            mDisposables.add(disposable);
        }

        return mImage;
    }

    @Override
    protected void onCleared() {
        mDisposables.dispose();
    }
}
