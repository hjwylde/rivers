package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public interface Section extends Serializable {
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
