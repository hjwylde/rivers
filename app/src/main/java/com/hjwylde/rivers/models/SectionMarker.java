package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SectionMarker implements ClusterItem {
    private final Section mSection;

    public SectionMarker(@NonNull Section section) {
        mSection = checkNotNull(section);
    }

    public Section getSection() {
        return mSection;
    }

    @Override
    public LatLng getPosition() {
        return mSection.getPutIn();
    }
}
