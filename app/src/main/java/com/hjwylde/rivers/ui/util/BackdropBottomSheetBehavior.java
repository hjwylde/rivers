package com.hjwylde.rivers.ui.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.hjwylde.rivers.R;

public final class BackdropBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private int mPeekHeight;

    public BackdropBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.BackdropBottomSheetBehavior_Layout);
        setPeekHeight(styledAttributes.getDimensionPixelSize(R.styleable.BackdropBottomSheetBehavior_Layout_behavior_peekHeight, 0));
        styledAttributes.recycle();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency.getId() == R.id.bottomSheet;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float collapsedY = dependency.getHeight() - mPeekHeight;
        float anchorPointY = child.getHeight();
        float expandedRatio = (dependency.getY() - anchorPointY) / (collapsedY - anchorPointY);

        float childY = Math.max(collapsedY * expandedRatio, 0);

        child.setY(childY);

        return true;
    }

    public void setPeekHeight(int peekHeight) {
        mPeekHeight = peekHeight;
    }
}