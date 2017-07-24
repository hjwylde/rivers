package com.hjwylde.rivers;

import android.app.Application;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.PasswordAuthorizer;
import com.couchbase.lite.replicator.Replication;
import com.hjwylde.rivers.db.services.CouchbaseRepository;
import com.hjwylde.rivers.services.Repository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
            // TODO (hjw): if this is a permission exception, then an error should be displayed to
            // the user. Otherwise, re-raise the exception.
            throw new RuntimeException(e);
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

            if (!BuildConfig.DEBUG) {
                String username = "android-" + BuildConfig.VERSION_NAME;
                String password = getString(R.string.database_password);

                Authenticator authenticator = new PasswordAuthorizer(username, password);
                push.setAuthenticator(authenticator);
                pull.setAuthenticator(authenticator);
            }

            push.start();
            pull.start();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpRepository() {
        mRepository = new CouchbaseRepository.Builder().database(mDatabase).build();
    }
}
