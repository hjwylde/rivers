package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.hjwylde.rivers.models.AbstractImage;
import com.hjwylde.rivers.models.Image;

import static com.hjwylde.rivers.db.models.BaseDocument.PROPERTY_TYPE;
import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class ImageDocument extends AbstractImage {
    @NonNull
    public static final String TYPE = "image";

    private static final long serialVersionUID = 1L;

    @NonNull
    private final Document mDocument;

    public ImageDocument(@NonNull Document document) {
        requireTrue(TYPE.equals(document.getProperty(PROPERTY_TYPE)));

        mDocument = requireNonNull(document);
    }

    public static Builder builder(@NonNull Database database) {
        return new Builder(database);
    }

    @NonNull
    @Override
    public String getData() {
        return (String) mDocument.getProperty(PROPERTY_DATA);
    }

    @NonNull
    @Override
    public String getId() {
        return (String) mDocument.getProperty(PROPERTY_ID);
    }

    public static final class Builder extends BaseDocument.Builder implements Image.Builder {
        private Builder(@NonNull Database database) {
            super(database, TYPE);
        }

        @NonNull
        public ImageDocument build() throws CouchbaseLiteException {
            return new ImageDocument(createOrUpdate());
        }

        @NonNull
        public Builder clone(@NonNull Image.Builder builder) {
            id(builder.id());
            data(builder.data());

            return this;
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

        @Override
        protected void validate() {
            requireTrue(TYPE.equals(type()));

            requireNonNull(mProperties.get(PROPERTY_DATA));
        }
    }
}