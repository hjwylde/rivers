package com.hjwylde.rivers.ui.util;


import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.activities.MapsActivity;
import com.hjwylde.rivers.ui.activities.MapsFragment;
import com.hjwylde.rivers.ui.contracts.MapsContract;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class CreateSectionMode implements ActionMode.Callback {
    private final BaseActivity mActivity;
    private final MapsFragment mMapsFragment;

    private MapsContract.View mView;

    private ActionMode mActionMode;

    private boolean mActive = false;

    public CreateSectionMode(@NonNull MapsActivity activity, @NonNull MapsFragment mapsFragment) {
        mActivity = checkNotNull(activity);
        mMapsFragment = checkNotNull(mapsFragment);

        mView = activity;
    }

    public void finish() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    public boolean isActive() {
        return mActive;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                GoogleMap map = mMapsFragment.getMap();

                if (map != null) {
                    LatLng putIn = map.getCameraPosition().target;
                    mView.createSection(putIn);

                    return true;
                }
        }

        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_maps_action_mode, menu);

        getFloatingActionButton().hide();
        animateCenterMarkerIn();

        mMapsFragment.disableOnMarkerClickListener();

        mActionMode = mode;

        mActive = true;

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getFloatingActionButton().show();
        animateCenterMarkerOut();

        mMapsFragment.enableOnMarkerClickListener();

        mActionMode = null;

        mActive = false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    private void animateCenterMarkerIn() {
        final View centerMarker = getCenterMarker();

        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_map_marker_in);
        animation.setAnimationListener(new NullAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                centerMarker.setVisibility(View.VISIBLE);
            }
        });

        centerMarker.startAnimation(animation);
    }

    private void animateCenterMarkerOut() {
        final View centerMarker = getCenterMarker();

        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_map_marker_out);
        animation.setAnimationListener(new NullAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                centerMarker.setVisibility(View.INVISIBLE);
            }
        });

        centerMarker.startAnimation(animation);
    }

    @NonNull
    private ImageView getCenterMarker() {
        return mActivity.findTById(R.id.center_marker);
    }

    @NonNull
    private FloatingActionButton getFloatingActionButton() {
        return mActivity.findTById(R.id.fab);
    }
}
