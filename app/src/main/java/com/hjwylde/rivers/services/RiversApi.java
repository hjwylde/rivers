package com.hjwylde.rivers.services;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface RiversApi {
    @GET("sections")
    Observable<List<Section>> getSections();

    @GET("sections/{id}")
    Observable<Section> getSection(@Path("id") String id);

    @GET("images/{id}")
    Observable<Image> getImage(@Path("id") String id);
}