package com.hjwylde.rivers.ui.activities.home;

import android.Manifest;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.hjwylde.reactivex.observers.LifecycleBoundObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static java.util.Objects.requireNonNull;

@UiThread
public final class MapFragment extends SupportMapFragment implements LifecycleRegistryOwner {
    private static final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private GoogleMap mMap;
    private ClusterManager<SectionMarker> mClusterManager;
    private GoogleMap.OnMapClickListener mOnMapClickListener;
    private ClusterManager.OnClusterItemClickListener<SectionMarker> mOnClusterItemClickListener;

    private MapViewModel mViewModel;

    private List<Section> mSections = new ArrayList<>();
    private Map<String, SectionMarker> mSectionMarkers = new HashMap<>();

    private boolean mClickEventsEnabled = true;

    public void animateCameraToSection(@NonNull String sectionId, @NonNull GoogleMap.CancelableCallback cancelableCallback) {
        LatLng position = mSectionMarkers.get(sectionId).getPosition();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, SectionClusterRenderer.MAX_MAP_ZOOM);

        // TODO (hjw): select the duration based on distance from current camera location
        mMap.animateCamera(update, 2000, cancelableCallback);
    }

    public void disableOnClickEvents() {
        mClickEventsEnabled = false;
    }

    public void enableOnClickEvents() {
        mClickEventsEnabled = true;
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (checkAccessLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            getMapAsync(this::onMapReady);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mViewModel.streamSections()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnGetSectionsObserver());
    }

    public void setOnMapClickListener(@NonNull GoogleMap.OnMapClickListener listener) {
        mOnMapClickListener = requireNonNull(listener);
    }

    public void setOnClusterItemClickListener(@NonNull ClusterManager.OnClusterItemClickListener<SectionMarker> listener) {
        mOnClusterItemClickListener = requireNonNull(listener);
    }

    private boolean checkAccessCoarseLocationPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
    }

    private boolean checkAccessFineLocationPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    private boolean checkAccessLocationPermission() {
        return checkAccessCoarseLocationPermission() && checkAccessFineLocationPermission();
    }

    private boolean onClusterClick(Cluster<SectionMarker> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 250 : 100;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        return true;
    }

    private void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setOnClusterItemClickListener(sectionMarker -> {
            if (mOnClusterItemClickListener != null) {
                return mOnClusterItemClickListener.onClusterItemClick(sectionMarker);
            }

            return false;
        });
        mClusterManager.setOnClusterClickListener(this::onClusterClick);

        final SectionClusterRenderer<SectionMarker> sectionClusterRenderer = new SectionClusterRenderer<>(getContext(), mMap, mClusterManager);
        sectionClusterRenderer.setMinClusterSize(1);
        mClusterManager.setRenderer(sectionClusterRenderer);

        mMap.setOnCameraIdleListener(() -> {
            sectionClusterRenderer.onCameraIdle();
            mClusterManager.onCameraIdle();
        });
        mMap.setOnMapClickListener(position -> {
            if (mOnMapClickListener != null) {
                mOnMapClickListener.onMapClick(position);
            }
        });
        mMap.setOnMarkerClickListener(marker -> {
            if (mClickEventsEnabled) {
                return mClusterManager.onMarkerClick(marker);
            }

            return true;
        });

        if (checkAccessLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestAccessLocationPermissions();
        }

        refreshMap();
    }

    private void refreshMap() {
        if (mMap == null) {
            return;
        }

        mMap.clear();
        mSectionMarkers.clear();
        mClusterManager.clearItems();

        for (Section section : mSections) {
            SectionMarker marker = new SectionMarker(section.getId(), section.getPutIn(), Section.Grade.from(section.getGrade()));

            mSectionMarkers.put(section.getId(), marker);
            mClusterManager.addItem(marker);
        }

        mClusterManager.cluster();
    }

    private void requestAccessLocationPermissions() {
        String[] locationPermissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        ActivityCompat.requestPermissions(getActivity(), locationPermissions, ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
    }

    @UiThread
    private final class OnGetSectionsObserver extends LifecycleBoundObserver<List<Section>> {
        OnGetSectionsObserver() {
            super(MapFragment.this);
        }

        @Override
        public void onComplete() {
            // Do nothing
        }

        @Override
        public void onError(@NonNull Throwable t) {
            // This should never happen
            throw new RuntimeException(t);
        }

        @Override
        public void onNext(@NonNull List<Section> sections) {
            mSections.clear();
            mSections.addAll(sections);

            refreshMap();
        }
    }
}