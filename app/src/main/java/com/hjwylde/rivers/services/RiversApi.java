package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface RiversApi {
    @NonNull
    @GET("images/{id}")
    Observable<Image> getImage(@Path("id") @NonNull String id);

    @NonNull
    @GET("sections")
    Observable<List<Section>> getSections();
}