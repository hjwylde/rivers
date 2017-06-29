package com.hjwylde.rivers.ui.viewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.lifecycle.LiveAction;
import com.hjwylde.lifecycle.MutableLiveAction;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class CreateSectionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final MutableLiveAction<Section> mCreateSection = new MutableLiveAction<>();
    private final MutableLiveAction<Image> mCreateImage = new MutableLiveAction<>();
    private final MutableLiveData<Image> mImage = new MutableLiveData<>();

    @NonNull
    public LiveAction<Image> createImage(@NonNull Image.Builder builder) {
        Disposable disposable = mRepository.createImage(builder)
                .subscribe(mCreateImage::postComplete, mCreateImage::postError);

        mDisposables.add(disposable);

        return mCreateImage;
    }

    @NonNull
    public LiveAction<Section> createSection(@NonNull Section.Builder builder) {
        Disposable disposable = mRepository.createSection(builder)
                .subscribe(mCreateSection::postComplete, mCreateSection::postError);

        mDisposables.add(disposable);

        return mCreateSection;
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
        mDisposables.clear();
    }
}
