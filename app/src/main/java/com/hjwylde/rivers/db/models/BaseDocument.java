package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

public abstract class BaseDocument {
    @NonNull
    public static final String PROPERTY_ID = "_id";
    @NonNull
    public static final String PROPERTY_TYPE = "type";
}