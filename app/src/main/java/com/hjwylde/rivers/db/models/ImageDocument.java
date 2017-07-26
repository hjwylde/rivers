package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.hjwylde.rivers.models.Image;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class ImageDocument extends BaseDocument implements Image {
    @NonNull
    public static final String TYPE = "image";

    @NonNull
    public static final String PROPERTY_DATA = "data";

    public ImageDocument(@NonNull Document document) {
        super(document);

        requireTrue(TYPE.equals(getType()));
    }

    public static Builder builder(@NonNull Database database) {
        return new Builder(database);
    }

    @NonNull
    @Override
    public String getData() {
        return (String) mDocument.getProperty(PROPERTY_DATA);
    }

    public static final class Builder extends BaseDocument.Builder implements Image.Builder {
        private Builder(@NonNull Database database) {
            super(database, TYPE);
        }

        @NonNull
        public Builder copy(@NonNull Image.Builder builder) {
            id(builder.id());
            data(builder.data());

            return this;
        }

        @NonNull
        @WorkerThread
        @Override
        public ImageDocument create() throws CouchbaseLiteException {
            return new ImageDocument(createDocument());
        }

        @NonNull
        @Override
        public Image.Builder data(String data) {
            mProperties.put(PROPERTY_DATA, data);

            return this;
        }

        @Override
        public String data() {
            return (String) mProperties.get(PROPERTY_DATA);
        }

        @NonNull
        @Override
        public Image.Builder id(String id) {
            mProperties.put(PROPERTY_ID, id);

            return this;
        }

        @NonNull
        @WorkerThread
        @Override
        public ImageDocument update() throws CouchbaseLiteException {
            return new ImageDocument(updateDocument());
        }

        @Override
        protected void validate() {
            requireTrue(TYPE.equals(type()));

            requireNonNull(data());
        }
    }
}