package com.hjwylde.rivers.ui.activities.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import static java.util.Objects.requireNonNull;

public final class SectionClusterRenderer<T extends SectionMarker> extends DefaultClusterRenderer<T> implements GoogleMap.OnCameraIdleListener {
    public static final float MAX_MAP_ZOOM = 10.0f;

    private GoogleMap mMap;
    private MarkerBitmapFactory mMarkerBitmapFactory;

    private float mCurrentMapZoom = 0.0f;

    public SectionClusterRenderer(@NonNull Context context, @NonNull GoogleMap map, @NonNull ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);

        mMap = requireNonNull(map);
        mMarkerBitmapFactory = new MarkerBitmapFactory(context);
    }

    @Override
    public void onCameraIdle() {
        mCurrentMapZoom = mMap.getCameraPosition().zoom;
    }

    @Override
    protected void onBeforeClusterItemRendered(T item, MarkerOptions markerOptions) {
        markerOptions.icon(getIcon(item));

        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        return super.shouldRenderAsCluster(cluster) && mCurrentMapZoom < MAX_MAP_ZOOM;
    }

    @NonNull
    private BitmapDescriptor createMarkerIcon(@ColorRes int colorResource) {
        return BitmapDescriptorFactory.fromBitmap(mMarkerBitmapFactory.fromColor(colorResource));
    }

    @NonNull
    private BitmapDescriptor getIcon(@NonNull T item) {
        int colorResource = item.getColorResource();
        Bitmap bitmap = mMarkerBitmapFactory.fromColor(colorResource);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}