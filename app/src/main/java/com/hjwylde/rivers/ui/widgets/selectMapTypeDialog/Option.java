package com.hjwylde.rivers.ui.widgets.selectMapTypeDialog;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class Option {
    @DrawableRes
    final int mIconId;
    @StringRes
    final int mLabelId;

    final int mMapType;

    Option(@DrawableRes int iconId, @StringRes int labelId, int mapType) {
        mIconId = iconId;
        mLabelId = labelId;

        mMapType = mapType;
    }

    public int getMapType() {
        return mMapType;
    }
}