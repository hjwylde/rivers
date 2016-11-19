package com.hjwylde.rivers.services;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface RiversApi {
    @GET("images/{id}")
    Observable<Image> getImage(@Path("id") @NonNull String id);

    @GET("sections/{id}")
    Observable<Section> getSection(@Path("id") @NonNull String id);

    @GET("sections")
    Observable<List<Section>> getSections();

    @POST("actions")
    Observable<Action> postAction(@Body @NonNull Action action);
}