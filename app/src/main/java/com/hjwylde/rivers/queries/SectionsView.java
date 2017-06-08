package com.hjwylde.rivers.queries;

import android.support.annotation.NonNull;

import com.couchbase.lite.Database;
import com.couchbase.lite.View;
import com.hjwylde.rivers.models.BaseDocument;
import com.hjwylde.rivers.models.Section;

public final class SectionsView {
    @NonNull
    private static final String NAME = "sections";
    @NonNull
    private static final String VERSION = "1";

    private static View mInstance;

    private SectionsView() {
    }

    @NonNull
    public static View getInstance(Database database) {
        if (mInstance == null) {
            mInstance = buildView(database);
        }

        return mInstance;
    }

    @NonNull
    private static View buildView(Database database) {
        View view = database.getView(NAME);
        if (view.getMap() == null) {
            view.setMap((document, emitter) -> {
                String type = (String) document.get(BaseDocument.PROPERTY_TYPE);
                if (Section.TYPE.equals(type)) {
                    emitter.emit(document.get(BaseDocument.PROPERTY_ID), null);
                }
            }, VERSION);
        }

        return view;
    }
}
