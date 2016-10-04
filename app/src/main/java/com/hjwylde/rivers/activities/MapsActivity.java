package com.hjwylde.rivers.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.RiversApplication;
import com.hjwylde.rivers.models.Section;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String LOG_TAG = "MapsActivity";

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private Subscription mSubscription;
    private Set<Section> mSections = new HashSet<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.google_maps_style));
        mMap.setOnMarkerClickListener(this);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);

        if (hasAccessFineLocationPermission()) {
            enableMyLocation();
        } else {
            requestAccessFineLocationPermission();
        }

        createMapMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, SectionActivity.class);
        intent.putExtra(SectionActivity.ID, (String) marker.getTag());

        startActivity(intent);

        return true;
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, R.string.new_action_coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        subscribe();
    }

    @Override
    protected void onDestroy() {
        // TODO (hjw): should this be in onPause?
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        super.onDestroy();
    }

    private void subscribe() {
        this.mSubscription = RiversApplication.getRiversApi()
                .getSections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Section>>() {
                    @Override
                    public void onCompleted() {
                        createMapMarkers();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO (hjw): handle error
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(List<Section> sections) {
                        mSections.addAll(sections);
                    }
                });
    }

    private void createMapMarkers() {
        if (mMap == null) {
            return;
        }

        // TODO (hjw): clear existing map markers

        Marker marker;
        for (Section section : mSections) {
            marker = mMap.addMarker(new MarkerOptions().position(section.getPutIn()));
            marker.setTag(section.getId());
        }
    }

    private boolean hasAccessFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED;
    }

    private void requestAccessFineLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
    }
}