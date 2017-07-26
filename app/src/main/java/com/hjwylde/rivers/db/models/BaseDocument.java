package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.hjwylde.rivers.util.Preconditions.requireNull;
import static java.util.Objects.requireNonNull;

abstract public class BaseDocument {
    @NonNull
    public static final String PROPERTY_ID = "_id";
    @NonNull
    public static final String PROPERTY_TYPE = "type";

    @NonNull
    protected final Document mDocument;

    BaseDocument(@NonNull Document document) {
        mDocument = requireNonNull(document);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BaseDocument)) {
            return false;
        }

        BaseDocument document = (BaseDocument) obj;

        if (!getType().equals(document.getType())) {
            return false;
        }

        return getId().equals(document.getId());
    }

    @NonNull
    public Document getDocument() {
        return mDocument;
    }

    @NonNull
    public String getId() {
        return (String) mDocument.getProperty(PROPERTY_ID);
    }

    @NonNull
    public String getType() {
        return (String) mDocument.getProperty(PROPERTY_TYPE);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    abstract static class Builder {
        @NonNull
        protected Database mDatabase;

        @NonNull
        protected Map<String, Object> mProperties = new HashMap<>();

        Builder(@NonNull Database database, @NonNull String type) {
            mDatabase = requireNonNull(database);

            mProperties.put(PROPERTY_TYPE, type);
        }

        @WorkerThread
        abstract public BaseDocument create() throws CouchbaseLiteException;

        public final String id() {
            return (String) mProperties.get(PROPERTY_ID);
        }

        public final String type() {
            return (String) mProperties.get(PROPERTY_TYPE);
        }

        @WorkerThread
        abstract public BaseDocument update() throws CouchbaseLiteException;

        @WorkerThread
        protected final Document createDocument() throws CouchbaseLiteException {
            requireNull(id());
            validate();

            mProperties.put(PROPERTY_ID, UUID.randomUUID().toString());

            Document document = mDatabase.getDocument(id());
            document.putProperties(mProperties);

            return document;
        }

        @WorkerThread
        protected final Document updateDocument() throws CouchbaseLiteException {
            requireNonNull(id());
            validate();

            Document document = mDatabase.getExistingDocument(id());
            document.update(newRevision -> {
                newRevision.setProperties(mProperties);

                return true;
            });

            return document;
        }

        abstract protected void validate();
    }
}