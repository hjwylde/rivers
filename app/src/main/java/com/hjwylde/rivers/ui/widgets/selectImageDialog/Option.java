package com.hjwylde.rivers.ui.widgets.selectImageDialog;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

abstract class Option implements View.OnClickListener {
    @DrawableRes
    final int mIconId;
    @StringRes
    final int mLabelId;

    Option(@DrawableRes int iconId, @StringRes int labelId) {
        mIconId = iconId;
        mLabelId = labelId;
    }
}