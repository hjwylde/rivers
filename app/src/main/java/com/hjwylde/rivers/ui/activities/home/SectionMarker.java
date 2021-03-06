package com.hjwylde.rivers.ui.activities.home;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;

import static java.util.Objects.requireNonNull;

public final class SectionMarker implements ClusterItem {
    @NonNull
    private final String mId;
    @NonNull
    private final LatLng mPutIn;
    @NonNull
    private final Section.Grade mGrade;

    public SectionMarker(@NonNull String id, @NonNull LatLng putIn, @NonNull Section.Grade grade) {
        mId = requireNonNull(id);
        mPutIn = requireNonNull(putIn);
        mGrade = requireNonNull(grade);
    }

    @ColorRes
    public int getColorResource() {
        switch (mGrade) {
            case I:
                return R.color.marker_grade_1;
            case II:
                return R.color.marker_grade_2;
            case III:
                return R.color.marker_grade_3;
            case IV:
                return R.color.marker_grade_4;
            case V:
                return R.color.marker_grade_5;
            default:
                return R.color.marker_grade_unknown;
        }
    }

    public Section.Grade getGrade() {
        return mGrade;
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