package com.hjwylde.rivers.db.views;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.couchbase.lite.Database;
import com.couchbase.lite.View;
import com.hjwylde.rivers.db.models.SectionDocument;

import static com.hjwylde.rivers.db.models.BaseDocument.PROPERTY_ID;
import static com.hjwylde.rivers.db.models.BaseDocument.PROPERTY_TYPE;
import static com.hjwylde.rivers.util.Preconditions.requireWorkerThread;

@WorkerThread
public final class SectionsView {
    @NonNull
    private static final String NAME = "sections";
    @NonNull
    private static final String VERSION = "1";

    private static View sInstance;

    private SectionsView() {
    }

    @NonNull
    public static View getInstance(Database database) {
        if (sInstance == null) {
            sInstance = buildView(database);
        }

        return sInstance;
    }

    @NonNull
    private static View buildView(Database database) {
        requireWorkerThread();

        View view = database.getView(NAME);
        if (view.getMap() == null) {
            view.setMap((document, emitter) -> {
                String type = (String) document.get(PROPERTY_TYPE);
                if (SectionDocument.TYPE.equals(type)) {
                    emitter.emit(document.get(PROPERTY_ID), null);
                }
            }, VERSION);
        }

        return view;
    }
}