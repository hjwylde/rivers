package com.hjwylde.rivers.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public final class Section implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("putIn")
    private SerializableLatLng mPutIn;
    @SerializedName("imageId")
    private String mImageId;
    @SerializedName("grade")
    private String mGrade;
    @SerializedName("length")
    private String mLength;
    @SerializedName("duration")
    private String mDuration;

    public String getImageId() {
        return mImageId;
    }

    public String getGrade() {
        return mGrade;
    }

    public String getLength() {
        return mLength;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public LatLng getPutIn() {
        return mPutIn.getLatLng();
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Section)) {
            return false;
        }

        return mId.equals(((Section) obj).getId());
    }
}
