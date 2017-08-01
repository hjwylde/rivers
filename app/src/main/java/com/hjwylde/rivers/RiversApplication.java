package com.hjwylde.rivers;

import android.app.Application;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.hjwylde.rivers.db.modules.DatabaseModule;
import com.hjwylde.rivers.db.modules.ReplicatorModule;
import com.hjwylde.rivers.db.services.CouchbaseRepository;
import com.hjwylde.rivers.db.services.Replicator;
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
        setUpDatabaseReplicator();

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
            mDatabase = DatabaseModule.provideDatabase(getApplicationContext());
        } catch (IOException | CouchbaseLiteException e) {
            // TODO (hjw): if this is a permission exception, then an error should be displayed to
            // the user. Otherwise, re-raise the exception.
            throw new RuntimeException(e);
        }
    }

    private void setUpDatabaseReplicator() {
        Replicator replicator = ReplicatorModule.provideReplicator(getApplicationContext(), mDatabase);
        replicator.start();
    }

    private void setUpRepository() {
        mRepository = new CouchbaseRepository.Builder().database(mDatabase).build();
    }
}