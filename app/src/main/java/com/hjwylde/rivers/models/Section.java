package com.hjwylde.rivers.models;

import com.google.android.gms.maps.model.LatLng;

public class Section {
    private int id;
    private String name;
    private String description;
    private LatLng putIn;
    private int image;

    public Section(int id, String name, String description, LatLng putIn, int image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.putIn = putIn;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getPutIn() {
        return putIn;
    }

    public int getImage() {
        return image;
    }
}
