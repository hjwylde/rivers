package com.hjwylde.rivers.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.couchbase.lite.Document;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.hjwylde.rivers.util.Preconditions.checkArgument;
import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class Image implements Serializable {
    @NonNull
    public static final String TYPE = "image";

    @NonNull
    public static final String PROPERTY_DATA = "data";

    private static final long serialVersionUID = 1L;

    @NonNull
    private final Map<String, Object> mProperties;

    public Image(@NonNull Map<String, Object> properties) {
        mProperties = new HashMap<>(properties);

        checkArgument(mProperties.get(BaseDocument.PROPERTY_TYPE).equals(TYPE));

        checkNotNull(mProperties.get(BaseDocument.PROPERTY_ID));
        checkNotNull(mProperties.get(PROPERTY_DATA));
    }

    @NonNull
    public Bitmap getBitmap() {
        byte[] decodedData = getDecodedData();

        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }

    @NonNull
    public String getData() {
        return (String) mProperties.get(PROPERTY_DATA);
    }

    @NonNull
    public String getId() {
        return (String) mProperties.get(BaseDocument.PROPERTY_ID);
    }

    @NonNull
    public Map<String, Object> getProperties() {
        return new HashMap<>(mProperties);
    }

    @NonNull
    private byte[] getDecodedData() {
        return Base64.decode(getData(), Base64.DEFAULT);
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
        public Image.Builder bitmap(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            String data = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            return data(data);
        }

        @NonNull
        public Image build() {
            return new Image(mProperties);
        }

        @NonNull
        public Image.Builder data(String data) {
            mProperties.put(PROPERTY_DATA, data);

            return this;
        }

        @NonNull
        public Image.Builder id(String id) {
            mProperties.put(BaseDocument.PROPERTY_ID, id);

            return this;
        }
    }
}