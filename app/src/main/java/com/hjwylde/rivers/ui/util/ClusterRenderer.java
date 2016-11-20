package com.hjwylde.rivers.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.hjwylde.rivers.R;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class ClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
    private Context mContext;

    private BitmapDescriptor mIcon;

    public ClusterRenderer(@NonNull Context context, @NonNull GoogleMap map, @NonNull ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);

        mContext = checkNotNull(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(T item, MarkerOptions markerOptions) {
        markerOptions.icon(getIcon());

        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<T> cluster) {
        return cluster.getSize() > 1;
    }

    @NonNull
    private Bitmap createMarkerBitmap() {
        Drawable drawable = getMarkerDrawable();
        int size = (int) getMarkerSize();

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.setColorFilter(getMarkerColor(), PorterDuff.Mode.MULTIPLY);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);

        return bitmap;
    }

    @NonNull
    private BitmapDescriptor createMarkerIcon() {
        return BitmapDescriptorFactory.fromBitmap(createMarkerBitmap());
    }

    @NonNull
    private BitmapDescriptor getIcon() {
        if (mIcon == null) {
            mIcon = createMarkerIcon();
        }

        return mIcon;
    }

    @ColorInt
    private int getMarkerColor() {
        return ContextCompat.getColor(mContext, R.color.accent);
    }

    @NonNull
    private Drawable getMarkerDrawable() {
        return ContextCompat.getDrawable(mContext, R.drawable.ic_map_marker);
    }

    @Dimension
    private float getMarkerSize() {
        return mContext.getResources().getDimension(R.dimen.markerSize);
    }
}