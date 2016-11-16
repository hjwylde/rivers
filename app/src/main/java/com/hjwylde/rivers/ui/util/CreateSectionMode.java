package com.hjwylde.rivers.ui.util;


import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.MapsFragment;
import com.hjwylde.rivers.ui.contracts.MapsContract;

import static com.google.common.base.Preconditions.checkNotNull;

public final class CreateSectionMode implements ActionMode.Callback {
    private final Context mContext;

    private final FloatingActionButton mFab;
    private final View mCenterMarker;
    private final MapsFragment mMapsFragment;

    private MapsContract.View mView;

    private boolean mActive = false;

    public CreateSectionMode(Context context, FloatingActionButton fab, View centerMarker, MapsFragment mapsFragment, MapsContract.View view) {
        mContext = checkNotNull(context);

        mFab = checkNotNull(fab);
        mCenterMarker = checkNotNull(centerMarker);
        mMapsFragment = checkNotNull(mapsFragment);

        mView = checkNotNull(view);
    }

    public boolean isActive() {
        return mActive;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_maps_action_mode, menu);

        mFab.hide();
        animateCenterMarkerIn();

        mMapsFragment.disableOnMarkerClickListener();

        mActive = true;

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                mView.onCreateSectionClick();
                return true;
        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mFab.show();
        animateCenterMarkerOut();

        mMapsFragment.enableOnMarkerClickListener();

        mActive = false;
    }

    private void animateCenterMarkerIn() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_map_marker_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mCenterMarker.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mCenterMarker.startAnimation(animation);
    }

    private void animateCenterMarkerOut() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_map_marker_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCenterMarker.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mCenterMarker.startAnimation(animation);
    }
}
