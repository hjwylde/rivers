package com.hjwylde.rivers;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.hjwylde.rivers.db.modules.DatabaseModule;
import com.hjwylde.rivers.db.modules.ReplicatorModule;
import com.hjwylde.rivers.db.services.CouchbaseRepository;
import com.hjwylde.rivers.db.services.Replicator;
import com.hjwylde.rivers.services.Repository;
import com.hjwylde.rivers.user.models.User;
import com.hjwylde.rivers.user.modules.UserModule;
import com.hjwylde.rivers.user.services.AuthenticatedRepositoryDecorator;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class RiversApplication extends Application {
    private static Database sDatabase;

    private static Repository sRepository;
    private static User sUser;

    @NonNull
    public static Repository getRepository() {
        return AuthenticatedRepositoryDecorator.decorate(sRepository, sUser);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setUpCalligraphy();

        setUpDatabase();
        setUpDatabaseReplicator();

        setUpRepository();
        setUpUser();
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

    private void setUpUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener((preferences, key) -> {
            if (key.equals(getString(R.string.pref_key_google_account_email))) {
                sUser = UserModule.provideUser(getApplicationContext());
            }
        });

        sUser = UserModule.provideUser(getApplicationContext());
    }
}