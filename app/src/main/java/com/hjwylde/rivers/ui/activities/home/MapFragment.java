package com.hjwylde.rivers.ui.activities.home;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import butterknife.BindDimen;
import butterknife.ButterKnife;
import io.reactivex.subjects.SingleSubject;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

@UiThread
public final class MapFragment extends SupportMapFragment implements LifecycleRegistryOwner {
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @BindDimen(R.dimen.map_paddingTop)
    @Dimension
    int mMapPaddingTop;
    @BindDimen(R.dimen.map_paddingRight)
    @Dimension
    int mMapPaddingRight;
    @BindDimen(R.dimen.map_paddingBottom)
    @Dimension
    int mMapPaddingBottom;
    @BindDimen(R.dimen.map_paddingLeft)
    @Dimension
    int mMapPaddingLeft;

    private SingleSubject<GoogleMap> mMapSubject = SingleSubject.create();
    private ClusterManager<SectionMarker> mClusterManager;
    private GoogleMap.OnMapClickListener mOnMapClickListener;
    private GoogleMap.OnMapLongClickListener mOnMapLongClickListener;
    private ClusterManager.OnClusterItemClickListener<SectionMarker> mOnClusterItemClickListener;

    private MapViewModel mViewModel;

    private Marker mDraggableMarker;
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

    public void disableDraggableMarker() {
        removeDraggableMarker();
    }

    public void disableOnClickEvents() {
        mClickEventsEnabled = false;
    }

    public void enableDraggableMarker(@NonNull LatLng position) {
        mMapSubject.subscribe(new LifecycleBoundSingleObserver<GoogleMap>(this) {
            @Override
            public void onError(Throwable t) {
                // Do nothing
            }

            @Override
            public void onSuccess(GoogleMap map) {
                addDraggableMarker(map, position);
            }
        });
    }

    public void enableOnClickEvents() {
        mClickEventsEnabled = true;
    }

    public Marker getDraggableMarker() {
        return mDraggableMarker;
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

        RxPermissions permissions = new RxPermissions(getActivity());
        permissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .delaySubscription(mMapSubject.toObservable())
                .subscribe(new OnRequestLocationPermissionObserver());
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);

        ButterKnife.bind(this, view);

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

        return view;
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

    public void setOnMapLongClickListener(@NonNull GoogleMap.OnMapLongClickListener listener) {
        mOnMapLongClickListener = requireNonNull(listener);
    }

    private void addDraggableMarker(GoogleMap map, LatLng position) {
        requireTrue(mDraggableMarker == null);

        MarkerBitmapFactory markerBitmapFactory = new MarkerBitmapFactory(getContext());
        Bitmap bitmap = markerBitmapFactory.fromColor(R.color.marker_grade_unknown);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Bitmap target = Bitmap.createBitmap(bitmap.getWidth() * 2, bitmap.getHeight() * 2, Bitmap.Config.ARGB_8888);
        RectF targetRect = new RectF();

        Canvas canvas = new Canvas(target);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(225);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();

            float mid = rect.right / 2;
            float left = mid * 2 - mid * scale;
            float top = rect.bottom * 2 - rect.bottom * scale;
            float right = mid * 2 + mid * scale;
            float bottom = rect.bottom * 2;
            targetRect.set(left, top, right, bottom);

            target.eraseColor(Color.TRANSPARENT);
            canvas.drawBitmap(bitmap, rect, targetRect, null);

            mDraggableMarker.setIcon(BitmapDescriptorFactory.fromBitmap(target));
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                MarkerOptions options = new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .draggable(true);

                mDraggableMarker = map.addMarker(options);
            }
        });

        animator.start();
    }

    private void initMap(@NonNull GoogleMap map) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        map.setPadding(mMapPaddingLeft, mMapPaddingTop, mMapPaddingRight, mMapPaddingBottom);

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
        map.setOnMapLongClickListener(position -> {
            if (mOnMapLongClickListener != null) {
                mOnMapLongClickListener.onMapLongClick(position);
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

        mMapSubject.getValue().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        return true;
    }

    private void removeDraggableMarker() {
        MarkerBitmapFactory markerBitmapFactory = new MarkerBitmapFactory(getContext());
        Bitmap bitmap = markerBitmapFactory.fromColor(R.color.marker_grade_unknown);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RectF targetRect = new RectF();

        Canvas canvas = new Canvas(target);

        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(195);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();

            float mid = rect.right / 2;
            float left = mid - mid * scale;
            float top = rect.bottom - rect.bottom * scale;
            float right = mid + mid * scale;
            float bottom = rect.bottom;
            targetRect.set(left, top, right, bottom);

            target.eraseColor(Color.TRANSPARENT);
            canvas.drawBitmap(bitmap, rect, targetRect, null);

            mDraggableMarker.setIcon(BitmapDescriptorFactory.fromBitmap(target));
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDraggableMarker.remove();
                mDraggableMarker = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });

        animator.start();
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
                GoogleMap map = mMapSubject.getValue();
                map.setMyLocationEnabled(true);
            }
        }
    }
}