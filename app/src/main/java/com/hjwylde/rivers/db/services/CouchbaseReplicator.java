package com.hjwylde.rivers.db.services;

import android.support.annotation.NonNull;

import com.couchbase.lite.Database;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.PasswordAuthorizer;
import com.couchbase.lite.replicator.Replication;

import java.net.URL;

import static java.util.Objects.requireNonNull;

public final class CouchbaseReplicator implements Replicator {
    @NonNull
    private final Database mDatabase;

    @NonNull
    private final URL mUrl;

    @NonNull
    private final Replication mPushReplication;
    @NonNull
    private final Replication mPullReplication;

    public CouchbaseReplicator(@NonNull Database database, @NonNull URL url, @NonNull String username, @NonNull String password) {
        mDatabase = requireNonNull(database);

        mUrl = requireNonNull(url);

        mPushReplication = mDatabase.createPushReplication(mUrl);
        mPushReplication.setContinuous(true);

        mPullReplication = mDatabase.createPullReplication(mUrl);
        mPullReplication.setContinuous(true);

        Authenticator authenticator = new PasswordAuthorizer(username, password);
        mPushReplication.setAuthenticator(authenticator);
        mPullReplication.setAuthenticator(authenticator);
    }

    @Override
    public void start() {
        mPushReplication.start();
        mPullReplication.start();
    }
}