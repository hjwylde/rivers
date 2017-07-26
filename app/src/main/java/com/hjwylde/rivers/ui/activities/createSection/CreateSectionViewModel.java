package com.hjwylde.rivers.ui.activities.createSection;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.Maybe;
import io.reactivex.Single;

public final class CreateSectionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    @NonNull
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        return mRepository.createImage(builder);
    }

    @NonNull
    public Single<Section> createSection(@NonNull Section.Builder builder) {
        return mRepository.createSection(builder);
    }

    @NonNull
    public Maybe<Image> getImage(@NonNull String id) {
        return mRepository.getImage(id);
    }

    @Override
    protected void onCleared() {
    }
}