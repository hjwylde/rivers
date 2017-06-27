package com.hjwylde.rivers;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.hjwylde.rivers.db.services.CouchbaseRepository;
import com.hjwylde.rivers.services.Repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RiversApplication extends Application {
    private static final String TAG = RiversApplication.class.getSimpleName();

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
        setUpDatabaseReplicators();

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

            String name = getString(R.string.database_name);

            DatabaseOptions options = new DatabaseOptions();
            options.setCreate(true);

            mDatabase = manager.openDatabase(name, options);
        } catch (IOException | CouchbaseLiteException e) {
            Log.e(TAG, e.getMessage(), e);

            // TODO (hjw): if this is a permission exception, then an error should be displayed to
            // the user. Otherwise, re-raise the exception.
        }
    }

    private void setUpDatabaseReplicators() {
        try {
            String spec = getString(R.string.database_url);
            URL url = new URL(spec);

            Replication push = mDatabase.createPushReplication(url);
            push.setContinuous(true);

            Replication pull = mDatabase.createPullReplication(url);
            pull.setContinuous(true);

            push.start();
            pull.start();
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);

            // TODO (hjw): re-raise the error, we should never get a malformed URL exception
        }
    }

    private void setUpRepository() {
        mRepository = new CouchbaseRepository.Builder().database(mDatabase).build();
    }
}
