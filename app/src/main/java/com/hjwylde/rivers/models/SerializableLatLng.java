package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class SerializableLatLng implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient LatLng mLatLng;

    public SerializableLatLng(double lat, double lng) {
        this(new LatLng(lat, lng));
    }

    public SerializableLatLng(LatLng latLng) {
        mLatLng = checkNotNull(latLng);
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