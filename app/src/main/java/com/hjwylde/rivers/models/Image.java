package com.hjwylde.rivers.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public interface Image {
    static Builder builder() {
        return new DefaultBuilder();
    }

    @NonNull
    default Bitmap getBitmap() {
        byte[] decodedData = getDecodedData();

        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }

    @NonNull
    String getData();

    @NonNull
    default byte[] getDecodedData() {
        return Base64.decode(getData(), Base64.DEFAULT);
    }

    @NonNull
    String getId();

    interface Builder {
        @NonNull
        default Builder bitmap(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            String data = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            return data(data);
        }

        @NonNull
        Image build() throws Exception;

        String data();

        @NonNull
        Builder data(String data);

        String id();

        @NonNull
        Builder id(String id);
    }

    class DefaultBuilder implements Builder, Serializable {
        private static final long serialVersionUID = 1L;

        protected String mId;
        protected String mData;

        protected DefaultBuilder() {
        }

        @NonNull
        @Override
        public Image build() {
            throw new UnsupportedOperationException();
        }

        @NonNull
        @Override
        public String data() {
            return mData;
        }

        @NonNull
        @Override
        public Builder data(String data) {
            mData = data;

            return this;
        }

        @Override
        public String id() {
            return mId;
        }

        @NonNull
        @Override
        public Builder id(String id) {
            mId = id;

            return this;
        }
    }
}