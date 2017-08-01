package com.hjwylde.rivers.db.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.hjwylde.rivers.R;

import java.io.IOException;

public final class DatabaseModule {
    private DatabaseModule() {
    }

    @NonNull
    public static Database provideDatabase(@NonNull Context context) throws IOException, CouchbaseLiteException {
        Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);

        String name = provideName(context);

        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);

        return manager.openDatabase(name, options);
    }

    @NonNull
    private static String provideName(@NonNull Context context) {
        return context.getString(R.string.database_name);
    }
}