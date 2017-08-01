package com.hjwylde.rivers.db.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.couchbase.lite.Database;
import com.hjwylde.rivers.db.services.NullReplicator;
import com.hjwylde.rivers.db.services.Replicator;

public final class ReplicatorModule {
    private ReplicatorModule() {
    }

    @NonNull
    public static Replicator provideReplicator(@NonNull Context context, @NonNull Database database) {
        return new NullReplicator();
    }
}