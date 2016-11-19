package com.hjwylde.rivers.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.models.SectionMarker;
import com.hjwylde.rivers.ui.contracts.MapsContract;
import com.hjwylde.rivers.ui.util.ClusterRenderer;

import java.util.Collection;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public final class MapsFragment extends SupportMapFragment implements OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<SectionMarker>, ClusterManager.OnClusterClickListener<SectionMarker> {
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private ClusterManager<SectionMarker> mClusterManager;
    private DefaultOnMarkerClickListener mOnMarkerClickListener = new DefaultOnMarkerClickListener();

    private MapsContract.View mView;

    public void disableOnMarkerClickListener() {
        mOnMarkerClickListener.disable();
    }

    public void enableOnMarkerClickListener() {
        mOnMarkerClickListener.enable();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mView = (MapsContract.View) activity;
    }

    @Override
    public boolean onClusterClick(Cluster<SectionMarker> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 250 : 100;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        return true;
    }

    @Override
    public boolean onClusterItemClick(SectionMarker sectionMarker) {
        mView.selectSection(sectionMarker.getSection());

        return true;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.maps_style));

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);

        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setRenderer(new ClusterRenderer<>(getContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(this);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);
        mMap.setOnMapClickListener(new DefaultOnMapClickListener());

        if (hasAccessFineLocationPermission()) {
            enableMyLocation();
        } else {
            requestAccessFineLocationPermission();
        }

        mView.refreshMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            getMapAsync(this);
        }
    }

    public void refreshMap(@NonNull Collection<Section> sections) {
        if (mMap == null) {
            return;
        }

        mMap.clear();
        mClusterManager.clearItems();

        for (Section section : sections) {
            mClusterManager.addItem(new SectionMarker(section));
        }

        mClusterManager.cluster();
    }

    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
    }

    private boolean hasAccessFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    private void requestAccessFineLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
    }

    private class DefaultOnMapClickListener implements GoogleMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng latLng) {
            mView.clearSelection();
        }
    }

    private class DefaultOnMarkerClickListener implements GoogleMap.OnMarkerClickListener {
        private boolean mEnabled = true;

        public void disable() {
            mEnabled = false;
        }

        public void enable() {
            mEnabled = true;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (mEnabled && mClusterManager != null) {
                return mClusterManager.onMarkerClick(marker);
            }

            return true;
        }
    }
}