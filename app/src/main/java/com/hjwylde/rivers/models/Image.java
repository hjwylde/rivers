package com.hjwylde.rivers.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

public final class Image {
    @SerializedName("id")
    private String mId;
    @SerializedName("data")
    private String mData;

    public String getId() {
        return mId;
    }

    public String getData() {
        return mData;
    }

    public byte[] getDecodedData() { return Base64.decode(mData, Base64.DEFAULT);
    }

    public Bitmap getBitmap() {
        byte[] decodedData = getDecodedData();

        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }
}