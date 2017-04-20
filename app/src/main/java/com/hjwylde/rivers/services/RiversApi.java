package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import rx.Observable;

public interface RiversApi {
    @NonNull
    Observable<Image> createImage(@NonNull Image.Builder builder);

    @NonNull
    Observable<Section> createSection(@NonNull Section.Builder builder);

    @NonNull
    Observable<Void> deleteSection(@NonNull Section section);

    @NonNull
    Observable<Image> getImage(@NonNull String id);

    @NonNull
    Observable<List<Section>> streamSections();
}