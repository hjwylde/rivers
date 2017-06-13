package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public interface Section extends Serializable {
    @NonNull
    String PROPERTY_ID = "_id";
    @NonNull
    String PROPERTY_TITLE = "title";
    @NonNull
    String PROPERTY_SUBTITLE = "subtitle";
    @NonNull
    String PROPERTY_DESCRIPTION = "description";
    @NonNull
    String PROPERTY_PUT_IN = "putIn";
    @NonNull
    String PROPERTY_PUT_IN_LATITUDE = "latitude";
    @NonNull
    String PROPERTY_PUT_IN_LONGITUDE = "longitude";
    @NonNull
    String PROPERTY_IMAGE_ID = "imageId";
    @NonNull
    String PROPERTY_GRADE = "grade";
    @NonNull
    String PROPERTY_LENGTH = "length";
    @NonNull
    String PROPERTY_DURATION = "duration";

    String getDescription();

    String getDuration();

    String getGrade();

    @NonNull
    String getId();

    String getImageId();

    String getLength();

    @NonNull
    LatLng getPutIn();

    String getSubtitle();

    @NonNull
    String getTitle();
}
