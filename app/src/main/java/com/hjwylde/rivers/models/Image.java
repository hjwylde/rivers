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

    @SerializedName("id")
    private final String mId;
    @SerializedName("data")
    private final String mData;

    public Image(@NonNull String id, @NonNull String data) {
        mId = checkNotNull(id);
        mData = checkNotNull(data);
    }

    public String getId() {
        return mId;
    }

    public String getData() {
        return mData;
    }

    public Bitmap getBitmap() {
        byte[] decodedData = getDecodedData();

        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }

    private byte[] getDecodedData() {
        return Base64.decode(mData, Base64.DEFAULT);
    }
}