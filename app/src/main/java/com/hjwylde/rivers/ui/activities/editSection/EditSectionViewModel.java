package com.hjwylde.rivers.ui.activities.editSection;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.services.Repository;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class EditSectionViewModel extends ViewModel {
    private final Repository mRepository = RiversApplication.getRepository();

    @NonNull
    public Single<Image> createImage(@NonNull Image.Builder builder) {
        return mRepository.createImage(builder)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Maybe<Image> getImage(@NonNull String id) {
        return mRepository.getImage(id)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<Section> updateSection(@NonNull Section.Builder builder) {
        return mRepository.updateSection(builder)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onCleared() {
    }
}