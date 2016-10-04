package com.hjwylde.rivers;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hjwylde.rivers.models.SerializableLatLng;
import com.hjwylde.rivers.services.RiversApi;
import com.hjwylde.rivers.util.LatLngDeserialiser;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class RiversApplication extends Application {
    private static RiversApi mRiversApi;

    public static RiversApi getRiversApi() {
        return mRiversApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mRiversApi = buildRiversApi();
    }

    private RiversApi buildRiversApi() {
        String url = getString(R.string.rivers_url);
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Gson gson = new GsonBuilder().registerTypeAdapter(SerializableLatLng.class, new LatLngDeserialiser()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RiversApi.class);
    }
}
