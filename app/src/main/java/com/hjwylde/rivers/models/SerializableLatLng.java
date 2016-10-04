package com.hjwylde.rivers.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerializableLatLng implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient LatLng mLatLng;

    public SerializableLatLng(double lat, double lng) {
        mLatLng = new LatLng(lat, lng);
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeDouble(mLatLng.latitude);
        out.writeDouble(mLatLng.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        mLatLng = new LatLng(in.readDouble(), in.readDouble());
    }
}