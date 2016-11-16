package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.presenters.MapsPresenter;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MapsActivity extends BaseActivity implements MapsContract.View, View.OnClickListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String STATE_ACTION_MODE_ACTIVE = "actionModeActive";
    private static final String STATE_SECTIONS = "sections";

    private boolean mActionModeActive = false;

    private MapsContract.Presenter mPresenter;

    private List<Section> mSections = new ArrayList<>();

    @Override
    public void setSections(@NonNull List<Section> sections) {
        mSections = checkNotNull(sections);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startCreateSectionMode();
                break;
        }
    }

    @Override
    public void startSectionActivity(@NonNull Section section) {
        Intent intent = new Intent(this, SectionActivity.class);
        intent.putExtra(SectionActivity.INTENT_SECTION, section);

        startActivity(intent);
    }

    public void refreshMap() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.refreshMap(mSections);
    }

    @Override
    public void onGetSectionsFailure(@NonNull Throwable t) {
        Log.w(TAG, t.getMessage(), t);

        // TODO (#23)
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_ACTION_MODE_ACTIVE, mActionModeActive);
        outState.putSerializable(STATE_SECTIONS, new ArrayList<>(mSections));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mActionModeActive = savedInstanceState.getBoolean(STATE_ACTION_MODE_ACTIVE);
        if (mActionModeActive) {
            startCreateSectionMode();
        }

        mSections = (List<Section>) savedInstanceState.getSerializable(STATE_SECTIONS);
        refreshMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Rivers);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mPresenter = new MapsPresenter(this, RiversApplication.getRiversService());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSections.isEmpty()) {
            mPresenter.getSections();
        }
    }

    @Override
    protected void onPause() {
        mPresenter.unsubscribe();

        super.onPause();
    }

    private void startCreateSectionMode() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        View centerMarker = findViewById(R.id.center_marker);
        MapsFragment mapsFragment = getMapsFragment();

        startActionMode(new CreateSectionMode(fab, centerMarker, mapsFragment));
    }

    private MapsFragment getMapsFragment() {
        return (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    private void startCreateSectionActivity() {
        MapsFragment mapsFragment = getMapsFragment();
        mapsFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Intent intent = new Intent(MapsActivity.this, CreateSectionActivity.class);
                intent.putExtra(CreateSectionActivity.INTENT_PUT_IN, googleMap.getCameraPosition().target);

                startActivity(intent);
            }
        });
    }

    private final class CreateSectionMode implements ActionMode.Callback {
        private final FloatingActionButton mFab;
        private final View mCenterMarker;
        private final MapsFragment mMapsFragment;

        public CreateSectionMode(FloatingActionButton fab, View centerMarker, MapsFragment mapsFragment) {
            mFab = fab;
            mCenterMarker = centerMarker;
            mMapsFragment = mapsFragment;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_maps_action_mode, menu);

            mFab.hide();
            animateCenterMarkerIn();

            mMapsFragment.disableOnMarkerClickListener();

            mActionModeActive = true;

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
                    startCreateSectionActivity();
                    return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFab.show();
            animateCenterMarkerOut();

            mMapsFragment.enableOnMarkerClickListener();

            mActionModeActive = false;
        }

        private void animateCenterMarkerIn() {
            Animation animation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.scale_map_marker_up);
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
            Animation animation = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.scale_map_marker_down);
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
}
