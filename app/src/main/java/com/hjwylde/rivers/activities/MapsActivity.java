package com.hjwylde.rivers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.models.Sections;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private Section[] mSections;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.google_maps_style));
        mMap.setOnMarkerClickListener(this);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);

        enableMyLocation();

        loadSections();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, SectionActivity.class);
        intent.putExtra(SectionActivity.ID, (int) marker.getTag());

        startActivity(intent);

        return true;
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, R.string.new_action_coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    private void loadSections() {
        mSections = Sections.MOCKED_SECTIONS;

        Marker marker;
        for (Section section : mSections) {
            marker = mMap.addMarker(new MarkerOptions().position(section.getPutIn()));
            marker.setTag(section.getId());
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
    }
}