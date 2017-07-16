package com.hjwylde.rivers.ui.viewModels;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.subjects.MaybeSubject;

public final class EditSectionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    private MaybeSubject<Image> mImageSubject = MaybeSubject.create();

    @NonNull
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        return mRepository.createImage(builder);
    }

    @NonNull
    public Maybe<Image> getImage(@NonNull String id) {
        if (mImageSubject.getValue() == null || !mImageSubject.getValue().getId().equals(id)) {
            mRepository.getImage(id)
                    .subscribe(mImageSubject);
        }

        return mImageSubject;
    }

    @NonNull
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        return mRepository.updateSection(builder);
    }

    @Override
    protected void onCleared() {
    }
}