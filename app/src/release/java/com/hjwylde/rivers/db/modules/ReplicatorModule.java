package com.hjwylde.rivers.db.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.couchbase.lite.Database;
import com.hjwylde.rivers.BuildConfig;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.db.services.CouchbaseReplicator;
import com.hjwylde.rivers.db.services.Replicator;

import java.net.MalformedURLException;
import java.net.URL;

public final class ReplicatorModule {
    private ReplicatorModule() {
    }

    @NonNull
    public static Replicator provideReplicator(@NonNull Context context, @NonNull Database database) {
        return new CouchbaseReplicator(database, provideUrl(context), provideUsername(), providePassword(context));
    }

    @NonNull
    private static String providePassword(@NonNull Context context) {
        // TODO (hjw): call this remote password or something similar
        return context.getString(R.string.database_password);
    }

    @NonNull
    private static URL provideUrl(Context context) {
        try {
            String spec = context.getString(R.string.database_url);

            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static String provideUsername() {
        return "android-" + BuildConfig.VERSION_NAME;
    }
}