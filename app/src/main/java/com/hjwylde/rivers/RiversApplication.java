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
    private static Database sDatabase;

    private static Repository sRepository;

    @NonNull
    public static Repository getRepository() {
        return sRepository;
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
            sDatabase = DatabaseModule.provideDatabase(getApplicationContext());
        } catch (IOException | CouchbaseLiteException e) {
            // TODO (hjw): if this is a permission exception, then an error should be displayed to
            // the user. Otherwise, re-raise the exception.
            throw new RuntimeException(e);
        }
    }

    private void setUpDatabaseReplicator() {
        Replicator replicator = ReplicatorModule.provideReplicator(getApplicationContext(), sDatabase);
        replicator.start();
    }

    private void setUpRepository() {
        sRepository = new CouchbaseRepository.Builder().database(sDatabase).build();
    }
}