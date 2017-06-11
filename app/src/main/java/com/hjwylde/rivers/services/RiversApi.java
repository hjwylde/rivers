package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.db.models.ImageDocument;
import com.hjwylde.rivers.db.models.SectionDocument;

import java.util.List;

import rx.Observable;

public interface RiversApi {
    @NonNull
    Observable<ImageDocument> createImage(@NonNull ImageDocument.Builder builder);

    @NonNull
    Observable<SectionDocument> createSection(@NonNull SectionDocument.Builder builder);

    @NonNull
    Observable<Void> deleteSection(@NonNull SectionDocument section);

    @NonNull
    Observable<ImageDocument> getImage(@NonNull String id);

    @NonNull
    Observable<List<SectionDocument>> searchSections(@NonNull String query);

    @NonNull
    Observable<List<SectionDocument>> streamSections();

    @NonNull
    Observable<SectionDocument> updateSection(@NonNull SectionDocument.Builder builder);
}