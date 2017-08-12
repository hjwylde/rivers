package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface Repository {
    @NonNull
    Single<Image> createImage(@NonNull Image.Builder builder);

    @NonNull
    Single<Section> createSection(@NonNull Section.Builder builder);

    @NonNull
    Completable deleteSection(@NonNull String id);

    @NonNull
    Maybe<Image> getImage(@NonNull String id);

    @NonNull
    Maybe<Section> getSection(@NonNull String id);

    @NonNull
    Observable<Section> getSections();

    @NonNull
    Flowable<Section> streamSection(@NonNull String id);

    @NonNull
    Flowable<List<Section>> streamSections();

    @NonNull
    Single<Section> updateSection(@NonNull Section.Builder builder);
}