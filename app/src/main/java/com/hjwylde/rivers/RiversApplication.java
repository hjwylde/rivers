package com.hjwylde.rivers;

import android.app.Application;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.hjwylde.rivers.db.services.CouchbaseRepository;
import com.hjwylde.rivers.services.Repository;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RiversApplication extends Application {
    private static Database mDatabase;

    private static Repository mRepository;

    @NonNull
    public static Repository getRepository() {
        return mRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUpCalligraphy();

        setUpDatabase();

        setUpRepository();
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

    private void setUpRepository() {
        mRepository = new CouchbaseRepository.Builder().database(mDatabase).build();
    }
}