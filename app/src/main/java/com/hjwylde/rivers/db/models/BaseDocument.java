package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public abstract class BaseDocument {
    @NonNull
    public static final String PROPERTY_ID = "_id";
    @NonNull
    public static final String PROPERTY_TYPE = "type";

    public abstract static class Builder {
        @NonNull
        protected Database mDatabase;

        @NonNull
        protected Map<String, Object> mProperties = new HashMap<>();

        protected Builder(@NonNull Database database, @NonNull String type) {
            mDatabase = requireNonNull(database);

            mProperties.put(PROPERTY_TYPE, type);
        }

        public String id() {
            return (String) mProperties.get(PROPERTY_ID);
        }

        public String type() {
            return (String) mProperties.get(PROPERTY_TYPE);
        }

        protected Document createOrUpdate() throws CouchbaseLiteException {
            validate();

            if (id() == null) {
                return create();
            } else {
                return update();
            }
        }

        abstract protected void validate();

        private Document create() throws CouchbaseLiteException {
            mProperties.put(PROPERTY_ID, UUID.randomUUID().toString());

            Document document = mDatabase.getDocument(id());
            document.putProperties(mProperties);

            return document;
        }

        private Document update() throws CouchbaseLiteException {
            Document document = mDatabase.getExistingDocument(id());
            document.update(newRevision -> {
                newRevision.setProperties(mProperties);

                return true;
            });

            return document;
        }
    }
}