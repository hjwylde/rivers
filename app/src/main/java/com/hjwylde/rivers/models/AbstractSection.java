package com.hjwylde.rivers.models;

abstract public class AbstractSection implements Section {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof com.hjwylde.rivers.models.Section)) {
            return false;
        }

        return getId().equals(((com.hjwylde.rivers.models.Section) obj).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
