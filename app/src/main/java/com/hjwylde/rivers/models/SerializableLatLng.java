package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;

public final class SerializableLatLng implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient LatLng mLatLng;

    public SerializableLatLng(double lat, double lng) {
        checkArgument(lat >= -90 && lat <= 90);
        checkArgument(lng >= -180 && lng <= 180);

        mLatLng = new LatLng(lat, lng);
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    private void writeObject(@NonNull ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeDouble(mLatLng.latitude);
        out.writeDouble(mLatLng.longitude);
    }

    private void readObject(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        mLatLng = new LatLng(in.readDouble(), in.readDouble());
    }
}