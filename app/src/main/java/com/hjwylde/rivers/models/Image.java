package com.hjwylde.rivers.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.Serializable;

public interface Image extends Serializable {
    @NonNull
    String PROPERTY_ID = "_id";
    @NonNull
    String PROPERTY_DATA = "data";

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
}