package com.hjwylde.rivers.ui.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hjwylde.rivers.R;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public class DropInAppBarBehavior<V extends View> extends CoordinatorLayout.Behavior<AppBarLayout> {
    private static final String TAG = DropInAppBarBehavior.class.getSimpleName();

    private final Context mContext;

    public DropInAppBarBehavior(@NonNull Context context, @NonNull AttributeSet attrs) {
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

    @Dimension
    private float getStatusBarHeight() {
        int id = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0) {
            return mContext.getResources().getDimension(id);
        }

        Log.w(TAG, "Unable to accurately determine the status bar height");

        return mContext.getResources().getDimension(R.dimen.statusBarHeight_fallback);
    }

    private void hideAppBar(@NonNull final AppBarLayout child) {
        if (child.getAlpha() < 1f) {
            return;
        }

        Animator alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 1, 0);
        alphaAnimator.setDuration(195);
        alphaAnimator.setInterpolator(new FastOutLinearInInterpolator());
        alphaAnimator.addListener(new NullAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                child.setVisibility(View.INVISIBLE);
            }
        });

        alphaAnimator.start();
    }

    private void showAppBar(@NonNull final AppBarLayout child) {
        if (child.getVisibility() == View.VISIBLE) {
            return;
        }

        Animator alphaAnimator = ObjectAnimator.ofFloat(child, "alpha", 0, 1);
        Animator yAnimator = ObjectAnimator.ofFloat(child, "y", 0, getStatusBarHeight());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnimator, yAnimator);
        set.setDuration(225);
        set.addListener(new NullAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                child.setVisibility(View.VISIBLE);
            }
        });

        set.start();
    }
}