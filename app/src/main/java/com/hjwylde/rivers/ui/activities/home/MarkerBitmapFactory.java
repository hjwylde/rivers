package com.hjwylde.rivers.ui.activities.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.LruCache;

import com.hjwylde.rivers.R;

import static java.util.Objects.requireNonNull;

public final class MarkerBitmapFactory {
    private final Context mContext;

    private final LruCache<Integer, Bitmap> mMarkerBitmaps;

    public MarkerBitmapFactory(@NonNull Context context) {
        mContext = requireNonNull(context);

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        mMarkerBitmaps = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @NonNull
    public Bitmap fromColor(@ColorRes int colorResource) {
        if (mMarkerBitmaps.get(colorResource) == null) {
            Drawable drawable = getMarkerDrawable();
            int size = (int) getMarkerSize();

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            drawable.setColorFilter(ContextCompat.getColor(mContext, colorResource), PorterDuff.Mode.MULTIPLY);
            drawable.setBounds(0, 0, size, size);
            drawable.draw(canvas);

            mMarkerBitmaps.put(colorResource, bitmap);
        }

        return mMarkerBitmaps.get(colorResource);
    }

    @NonNull
    private Drawable getMarkerDrawable() {
        return ContextCompat.getDrawable(mContext, R.drawable.ic_map_marker);
    }

    @Dimension
    private float getMarkerSize() {
        return mContext.getResources().getDimension(R.dimen.icon_large);
    }
}