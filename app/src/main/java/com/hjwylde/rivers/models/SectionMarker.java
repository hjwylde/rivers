package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class SectionMarker implements ClusterItem {
    private final Section mSection;

    public SectionMarker(@NonNull Section section) {
        mSection = checkNotNull(section);
    }

    @Override
    public LatLng getPosition() {
        return mSection.getPutIn();
    }

    public Section getSection() {
        return mSection;
    }
}
