package com.hjwylde.rivers.ui.activities.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
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
import com.hjwylde.reactivex.observers.LifecycleBoundSingleObserver;
import com.hjwylde.rivers.R;
import com.hjwylde.rivers.models.Section;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.subjects.SingleSubject;

import static java.util.Objects.requireNonNull;

@UiThread
public final class MapFragment extends SupportMapFragment implements LifecycleRegistryOwner {
    private static final String TAG = MapFragment.class.getSimpleName();

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    private SingleSubject<GoogleMap> mMapSubject = SingleSubject.create();
    private ClusterManager<SectionMarker> mClusterManager;
    private GoogleMap.OnMapClickListener mOnMapClickListener;
    private ClusterManager.OnClusterItemClickListener<SectionMarker> mOnClusterItemClickListener;

    private MapViewModel mViewModel;

    private Map<String, SectionMarker> mSectionMarkers = new HashMap<>();

    private boolean mClickEventsEnabled = true;

    public void animateCameraToSection(@NonNull String sectionId, @NonNull GoogleMap.CancelableCallback cancelableCallback) {
        mMapSubject.subscribe(new LifecycleBoundSingleObserver<GoogleMap>(this) {
            @Override
            public void onError(Throwable t) {
                // Do nothing
            }

            @Override
            public void onSuccess(GoogleMap map) {
                LatLng position = mSectionMarkers.get(sectionId).getPosition();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, SectionClusterRenderer.MAX_MAP_ZOOM);

                // TODO (hjw): select the duration based on distance from current camera location
                map.animateCamera(update, 2000, cancelableCallback);
            }
        });
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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);

        getMapAsync(map -> mMapSubject.onSuccess(map));
        mMapSubject.subscribe(new LifecycleBoundSingleObserver<GoogleMap>(this) {
            @Override
            public void onError(Throwable t) {
                // Do nothing
            }

            @Override
            public void onSuccess(GoogleMap map) {
                initMap(map);
            }
        });

        RxPermissions permissions = new RxPermissions(getActivity());
        permissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .delaySubscription(mMapSubject.toObservable())
                .subscribe(new OnRequestLocationPermissionObserver());
    }

    @Override
    public void onStart() {
        super.onStart();

        mViewModel.streamSections()
                .delaySubscription(mMapSubject.toObservable())
                .subscribe(new OnGetSectionsObserver());
    }

    public void setOnClusterItemClickListener(@NonNull ClusterManager.OnClusterItemClickListener<SectionMarker> listener) {
        mOnClusterItemClickListener = requireNonNull(listener);
    }

    public void setOnMapClickListener(@NonNull GoogleMap.OnMapClickListener listener) {
        mOnMapClickListener = requireNonNull(listener);
    }

    private void initMap(GoogleMap map) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        mClusterManager = new ClusterManager<>(getContext(), map);
        mClusterManager.setOnClusterItemClickListener(sectionMarker -> {
            if (mOnClusterItemClickListener != null) {
                return mOnClusterItemClickListener.onClusterItemClick(sectionMarker);
            }

            return false;
        });
        mClusterManager.setOnClusterClickListener(this::onClusterClick);

        final SectionClusterRenderer<SectionMarker> sectionClusterRenderer = new SectionClusterRenderer<>(getContext(), map, mClusterManager);
        sectionClusterRenderer.setMinClusterSize(1);
        mClusterManager.setRenderer(sectionClusterRenderer);

        map.setOnCameraIdleListener(() -> {
            sectionClusterRenderer.onCameraIdle();
            mClusterManager.onCameraIdle();
        });
        map.setOnMapClickListener(position -> {
            if (mOnMapClickListener != null) {
                mOnMapClickListener.onMapClick(position);
            }
        });
        map.setOnMarkerClickListener(marker -> {
            if (mClickEventsEnabled) {
                return mClusterManager.onMarkerClick(marker);
            }

            return true;
        });
    }

    private boolean onClusterClick(Cluster<SectionMarker> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 250 : 100;
        mMapSubject.getValue().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        return true;
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
            refreshMap(sections);
        }

        private void refreshMap(@NonNull List<Section> sections) {
            mMapSubject.getValue().clear();
            mSectionMarkers.clear();
            mClusterManager.clearItems();

            for (Section section : sections) {
                SectionMarker marker = new SectionMarker(section.getId(), section.getPutIn(), Section.Grade.from(section.getGrade()));

                mSectionMarkers.put(section.getId(), marker);
                mClusterManager.addItem(marker);
            }

            mClusterManager.cluster();
        }
    }

    private final class OnRequestLocationPermissionObserver extends LifecycleBoundObserver<Boolean> {
        OnRequestLocationPermissionObserver() {
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

        @SuppressLint("MissingPermission")
        @Override
        public void onNext(@NonNull Boolean granted) {
            if (granted) {
                mMapSubject.getValue().setMyLocationEnabled(true);
            } else {
                Log.i(TAG, "Request for location permission denied");
            }
        }
    }
}