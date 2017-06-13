package com.hjwylde.rivers.models;

abstract public class AbstractImage implements Image {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Image)) {
            return false;
        }

        return getId().equals(((Image) obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}