package com.hjwylde.rivers.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class Image implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    @SerializedName("id")
    private final String mId;
    @NonNull
    @SerializedName("data")
    private final String mData;

    public Image(@NonNull String id, @NonNull String data) {
        mId = checkNotNull(id);
        mData = checkNotNull(data);
    }

    @NonNull
    public Bitmap getBitmap() {
        byte[] decodedData = getDecodedData();

        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }

    @NonNull
    public String getData() {
        return mData;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    private byte[] getDecodedData() {
        return Base64.decode(mData, Base64.DEFAULT);
    }
}