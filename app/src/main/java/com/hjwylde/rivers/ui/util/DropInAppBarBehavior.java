package com.hjwylde.rivers.ui.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.hjwylde.rivers.R;

public class DropInAppBarBehavior<V extends View> extends CoordinatorLayout.Behavior<AppBarLayout> {
    private boolean mInit = false;

    private ObjectAnimator mAnimator;

    public DropInAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        return dependency.getId() == R.id.bottomSheet;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, AppBarLayout child, View dependency) {
        if (!mInit) {
            init(child);
        }

        if (!isVisible(child) && dependency.getY() <= 0) {
            showAppBar(child);
        } else if (isVisible(child) && dependency.getY() > 0) {
            hideAppBar(child);
        }

        return true;
    }

    private boolean isVisible(AppBarLayout view) {
        return view.getY() > -view.getHeight() && view.getVisibility() == View.VISIBLE;
    }

    private void init(AppBarLayout child) {
        child.setY(-child.getHeight());

        mInit = true;
    }

    private void hideAppBar(final AppBarLayout child) {
        if (child.getAlpha() < 1.0f) {
            return;
        } else if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(child, "alpha", 1.0f, 0.0f);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled = false;

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCancelled) {
                    child.setAlpha(1.0f);
                    child.setY(-child.getHeight());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCancelled = true;
            }
        });
        mAnimator.start();
    }

    private void showAppBar(AppBarLayout child) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ObjectAnimator.ofFloat(child, "y", -child.getHeight(), 0.0f);
        mAnimator.start();
    }
}