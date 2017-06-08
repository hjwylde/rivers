package com.hjwylde.rivers;

import android.app.Application;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.hjwylde.rivers.services.LocalRiversService;
import com.hjwylde.rivers.services.RiversApi;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RiversApplication extends Application {
    private static Database mDatabase;

    private static RiversApi mLocalRiversService;

    @NonNull
    public static RiversApi getRiversService() {
        return mLocalRiversService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUpCalligraphy();

        setUpDatabase();

        setUpLocalRiversService();
    }

    private void setUpCalligraphy() {
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    private void setUpDatabase() {
        try {
            Manager manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);

            DatabaseOptions options = new DatabaseOptions();
            options.setCreate(true);

            mDatabase = manager.openDatabase("rivers", options);
        } catch (IOException | CouchbaseLiteException e) {
            e.printStackTrace();
            // TODO (hjw): panic
        }
    }

    private void setUpLocalRiversService() {
        mLocalRiversService = new LocalRiversService.Builder().database(mDatabase).build();
    }
}