package com.hjwylde.rivers.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.presenters.MapsPresenter;
import com.hjwylde.rivers.ui.util.CreateSectionMode;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class MapsActivity extends BaseActivity implements MapsContract.View, View.OnClickListener {
    private static final String TAG = MapsActivity.class.getSimpleName();

    private static final String STATE_CREATE_SECTION_MODE_ACTIVE = "createSectionModeActive";
    private static final String STATE_SECTIONS = "sections";

    private CreateSectionMode mCreateSectionMode;

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

    @Override
    public void onCreateSectionClick() {
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

        outState.putBoolean(STATE_CREATE_SECTION_MODE_ACTIVE, mCreateSectionMode != null && mCreateSectionMode.isActive());
        outState.putSerializable(STATE_SECTIONS, new ArrayList<>(mSections));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boolean createSectionModeActive = savedInstanceState.getBoolean(STATE_CREATE_SECTION_MODE_ACTIVE);
        if (createSectionModeActive) {
            startCreateSectionMode();
        }

        mSections = (List<Section>) savedInstanceState.getSerializable(STATE_SECTIONS);
        refreshMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Rivers_Dark_NoActionBar);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = getFloatingActionButton();
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
        if (mCreateSectionMode == null) {
            FloatingActionButton fab = getFloatingActionButton();
            View centerMarker = findViewById(R.id.center_marker);
            MapsFragment mapsFragment = getMapsFragment();

            mCreateSectionMode = new CreateSectionMode(this, fab, centerMarker, mapsFragment, this);
        }

        startActionMode(mCreateSectionMode);
    }

    private FloatingActionButton getFloatingActionButton() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }

    private MapsFragment getMapsFragment() {
        return (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }
}
