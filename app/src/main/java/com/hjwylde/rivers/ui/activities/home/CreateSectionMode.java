package com.hjwylde.rivers.ui.activities.home;


import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.BaseActivity;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class CreateSectionMode implements ActionMode.Callback {
    private final BaseActivity mActivity;
    private final MapFragment mMapFragment;

    private final HomeContract.View mView;

    private LatLng mPosition;

    private ActionMode mActionMode;
    private boolean mActive = false;


    public CreateSectionMode(@NonNull HomeActivity activity, @NonNull MapFragment mapFragment, @NonNull LatLng position) {
        mActivity = requireNonNull(activity);
        mMapFragment = requireNonNull(mapFragment);

        mView = activity;

        mPosition = requireNonNull(position);
    }

    public void finish() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    public LatLng getPosition() {
        return mMapFragment.getDraggableMarker().getPosition();
    }

    public void setPosition(@NonNull LatLng position) {
        mPosition = requireNonNull(position);

        mMapFragment.getDraggableMarker().setPosition(position);
    }

    public boolean isActive() {
        return mActive;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                LatLng putIn = mMapFragment.getDraggableMarker().getPosition();

                mView.createSection(putIn);

                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        requireTrue(mActionMode == null);

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_home_create_section_mode, menu);

        getFloatingActionButton().hide();

        mMapFragment.enableDraggableMarker(mPosition);
        mMapFragment.disableOnClickEvents();

        mActionMode = mode;
        mActive = true;

        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getFloatingActionButton().show();

        mMapFragment.disableDraggableMarker();
        mMapFragment.enableOnClickEvents();

        mActionMode = null;
        mActive = false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @NonNull
    private FloatingActionButton getFloatingActionButton() {
        return mActivity.findTById(R.id.fab);
    }
}
