package com.hjwylde.rivers.ui.util;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.hjwylde.rivers.R;

public final class BottomSheetAwareBehavior extends FloatingActionButton.Behavior {
    public BottomSheetAwareBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            return super.onDependentViewChanged(parent, child, dependency);
        } else if (dependency.getId() == R.id.bottomSheet) {
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(dependency);

            switch (bottomSheetBehavior.getState()) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    child.show();
                    break;
                default:
                    child.hide();
            }
        }

        return false;
    }
}
