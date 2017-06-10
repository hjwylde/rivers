package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import static java.util.Objects.requireNonNull;

public final class SectionMarker implements ClusterItem {
    @NonNull
    private final Section mSection;

    public SectionMarker(@NonNull Section section) {
        mSection = requireNonNull(section);
    }

    @Override
    public LatLng getPosition() {
        return mSection.getPutIn();
    }

    @NonNull
    public Section getSection() {
        return mSection;
    }
}
