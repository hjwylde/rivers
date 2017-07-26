package com.hjwylde.rivers.ui.activities.maps;

import android.animation.Animator;

public abstract class NullAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationCancel(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }
}
