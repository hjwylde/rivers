package com.hjwylde.rivers;

import android.app.Application;

import com.hjwylde.rivers.services.RemoteRiversServiceBuilder;
import com.hjwylde.rivers.services.RiversApi;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RiversApplication extends Application {
    private static RiversApi mRemoteRiversService;

    public static RiversApi getRiversService() {
        return mRemoteRiversService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUpCalligraphy();

        setUpRemoteRiversService();
    }

    private void setUpCalligraphy() {
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    private void setUpRemoteRiversService() {
        mRemoteRiversService = new RemoteRiversServiceBuilder().context(getApplicationContext()).build();
    }
}