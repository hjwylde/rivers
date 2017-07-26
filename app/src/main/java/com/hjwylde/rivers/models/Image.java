package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Base64;

import java.io.Serializable;

public interface Image {
    @NonNull
    static DefaultBuilder builder() {
        return new DefaultBuilder();
    }

    @NonNull
    String getData();

    @NonNull
    @WorkerThread
    default byte[] getDecodedData() {
        return Base64.decode(getData(), Base64.DEFAULT);
    }

    @NonNull
    String getId();

    interface Builder {
        String data();

        @NonNull
        Builder data(String data);

        @NonNull
        @WorkerThread
        default Builder decodedData(byte[] decodedData) {
            return data(Base64.encodeToString(decodedData, Base64.DEFAULT));
        }

        String id();

        @NonNull
        Builder id(String id);
    }

    final class DefaultBuilder implements Builder, Serializable {
        private static final long serialVersionUID = 1L;

        private String mId;
        private String mData;

        private DefaultBuilder() {
        }

        @NonNull
        @Override
        public String data() {
            return mData;
        }

        @NonNull
        @Override
        public DefaultBuilder data(String data) {
            mData = data;

            return this;
        }

        @Override
        public String id() {
            return mId;
        }

        @NonNull
        @Override
        public DefaultBuilder id(String id) {
            mId = id;

            return this;
        }
    }
}