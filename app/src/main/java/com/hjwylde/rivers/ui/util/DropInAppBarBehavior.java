package com.hjwylde.rivers.ui.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.hjwylde.rivers.R;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public class DropInAppBarBehavior<V extends View> extends CoordinatorLayout.Behavior<AppBarLayout> {
    private final Context mContext;

    public DropInAppBarBehavior(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = checkNotNull(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        return dependency.getId() == R.id.bottomSheet;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        if (dependency.getY() <= 0) {
            showAppBar(child);
        } else if (dependency.getY() > 0) {
            hideAppBar(child);
        }

        return true;
    }

    private int getStatusBarHeight() {
        int id = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            return (int) mContext.getResources().getDimension(id);
        }

        return (int) mContext.getResources().getDimension(R.dimen.statusBarHeight_fallback);
    }

    private void hideAppBar(AppBarLayout child) {
        if (child.getAlpha() < 1f) {
            return;
        }

        Animator alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 1, 0);
        alphaAnimator.setDuration(195);
        alphaAnimator.setInterpolator(new FastOutLinearInInterpolator());
        alphaAnimator.start();
    }

    private void showAppBar(AppBarLayout child) {
        if (child.getAlpha() > 0f) {
            return;
        }

        Animator alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 0, 1);
        Animator yAnimator = ObjectAnimator.ofFloat(child, "y", -getStatusBarHeight(), 0);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator, yAnimator);
        set.setDuration(225);
        set.start();
    }
}