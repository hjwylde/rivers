package com.hjwylde.rivers.db.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.couchbase.lite.Document;
import com.hjwylde.rivers.models.AbstractImage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class ImageDocument extends AbstractImage {
    @NonNull
    public static final String TYPE = "image";

    private static final long serialVersionUID = 1L;

    @NonNull
    private final Map<String, Object> mProperties;

    public ImageDocument(@NonNull Map<String, Object> properties) {
        mProperties = new HashMap<>(properties);

        requireTrue(mProperties.get(BaseDocument.PROPERTY_TYPE).equals(TYPE));

        requireNonNull(mProperties.get(BaseDocument.PROPERTY_ID));
        requireNonNull(mProperties.get(PROPERTY_DATA));
    }

    @NonNull
    @Override
    public String getData() {
        return (String) mProperties.get(PROPERTY_DATA);
    }

    @NonNull
    @Override
    public String getId() {
        return (String) mProperties.get(BaseDocument.PROPERTY_ID);
    }

    @NonNull
    public Map<String, Object> getProperties() {
        return new HashMap<>(mProperties);
    }


    public static final class Builder {
        @NonNull
        private final Map<String, Object> mProperties;

        public Builder() {
            mProperties = new HashMap<>();
            mProperties.put(BaseDocument.PROPERTY_TYPE, TYPE);
        }

        public Builder(@NonNull Map<String, Object> properties) {
            mProperties = new HashMap<>(properties);
        }

        public Builder(@NonNull Document document) {
            this(document.getProperties());
        }

        @NonNull
        public ImageDocument.Builder bitmap(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            String data = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            return data(data);
        }

        @NonNull
        public ImageDocument build() {
            return new ImageDocument(mProperties);
        }

        @NonNull
        public ImageDocument.Builder data(String data) {
            mProperties.put(PROPERTY_DATA, data);

            return this;
        }

        @NonNull
        public ImageDocument.Builder id(String id) {
            mProperties.put(BaseDocument.PROPERTY_ID, id);

            return this;
        }
    }
}