package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.ImageDocument;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import rx.Observable;

public interface RiversApi {
    @NonNull
    Observable<ImageDocument> createImage(@NonNull ImageDocument.Builder builder);

    @NonNull
    Observable<Section> createSection(@NonNull Section.Builder builder);

    @NonNull
    Observable<Void> deleteSection(@NonNull Section section);

    @NonNull
    Observable<ImageDocument> getImage(@NonNull String id);

    @NonNull
    Observable<List<Section>> searchSections(@NonNull String query);

    @NonNull
    Observable<List<Section>> streamSections();

    @NonNull
    Observable<Section> updateSection(@NonNull Section.Builder builder);
}