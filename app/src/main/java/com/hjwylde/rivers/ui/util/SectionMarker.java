package com.hjwylde.rivers.ui.util;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import static java.util.Objects.requireNonNull;

public final class SectionMarker implements ClusterItem {
    @NonNull
    private final String mId;
    @NonNull
    private final LatLng mPutIn;

    public SectionMarker(@NonNull String id, @NonNull LatLng putIn) {
        mId = requireNonNull(id);
        mPutIn = requireNonNull(putIn);
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mPutIn;
    }
}