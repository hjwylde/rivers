package com.hjwylde.rivers.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.SerializableLatLng;
import com.hjwylde.rivers.models.serialisation.LatLngDeserializer;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class RemoteRiversServiceBuilder {
    private Context mContext;

    public RemoteRiversServiceBuilder context(@NonNull Context context) {
        mContext = checkNotNull(context);

        return this;
    }

    public RiversApi build() {
        return getRetrofit().create(RiversApi.class);
    }

    private RxJavaCallAdapterFactory getRxAdapter() {
        return RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
    }

    private Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(getUrl())
                .addCallAdapterFactory(getRxAdapter())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
    }

    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(SerializableLatLng.class, new LatLngDeserializer())
                .create();
    }

    private String getUrl() {
        return mContext.getString(R.string.rivers_url);
    }
}
