package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class Section implements Serializable {
    @NonNull
    public static final String PROPERTY_ID = "id";
    @NonNull
    public static final String PROPERTY_TITLE = "title";
    @NonNull
    public static final String PROPERTY_SUBTITLE = "subtitle";
    @NonNull
    public static final String PROPERTY_DESCRIPTION = "description";
    @NonNull
    public static final String PROPERTY_PUT_IN = "putIn";
    @NonNull
    public static final String PROPERTY_IMAGE_ID = "imageId";
    @NonNull
    public static final String PROPERTY_GRADE = "grade";
    @NonNull
    public static final String PROPERTY_LENGTH = "length";
    @NonNull
    public static final String PROPERTY_DURATION = "duration";

    private static final long serialVersionUID = 1L;

    @NonNull
    @SerializedName(PROPERTY_ID)
    private final String mId;
    @NonNull
    @SerializedName(PROPERTY_TITLE)
    private final String mTitle;
    @SerializedName(PROPERTY_SUBTITLE)
    private final String mSubtitle;
    @SerializedName(PROPERTY_DESCRIPTION)
    private final String mDescription;
    @NonNull
    @SerializedName(PROPERTY_PUT_IN)
    private final SerializableLatLng mPutIn;
    @SerializedName(PROPERTY_IMAGE_ID)
    private final String mImageId;
    @SerializedName(PROPERTY_GRADE)
    private final String mGrade;
    @SerializedName(PROPERTY_LENGTH)
    private final String mLength;
    @SerializedName(PROPERTY_DURATION)
    private final String mDuration;

    public Section(@NonNull String id, @NonNull String title, String subtitle, String description, @NonNull LatLng putIn, String imageId, String grade, String length, String duration) {
        this(id, title, subtitle, description, new SerializableLatLng(putIn), imageId, grade, length, duration);
    }

    public Section(@NonNull String id, @NonNull String title, String subtitle, String description, @NonNull SerializableLatLng putIn, String imageId, String grade, String length, String duration) {
        mId = checkNotNull(id);
        mTitle = checkNotNull(title);
        mSubtitle = subtitle;
        mDescription = description;
        mPutIn = checkNotNull(putIn);
        mImageId = imageId;
        mGrade = grade;
        mLength = length;
        mDuration = duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Section)) {
            return false;
        }

        return mId.equals(((Section) obj).getId());
    }

    public String getDescription() {
        return mDescription;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getGrade() {
        return mGrade;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public String getImageId() {
        return mImageId;
    }

    public String getLength() {
        return mLength;
    }

    @NonNull
    public LatLng getPutIn() {
        return mPutIn.getLatLng();
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    public static final class Builder implements Serializable {
        private static final long serialVersionUID = 1L;

        private String mId;
        private String mTitle;
        private String mSubtitle;
        private String mDescription;
        private SerializableLatLng mPutIn;
        private String mImageId;
        private String mGrade;
        private String mLength;
        private String mDuration;

        public Builder() {
        }

        public Builder(@NonNull Section section) {
            mId = section.getId();
            mTitle = section.getTitle();
            mSubtitle = section.getSubtitle();
            mDescription = section.getDescription();
            mPutIn = new SerializableLatLng(section.getPutIn());
            mImageId = section.getImageId();
            mGrade = section.getGrade();
            mLength = section.getLength();
            mDuration = section.getDuration();
        }

        public Builder(@NonNull Builder builder) {
            mId = builder.id();
            mTitle = builder.title();
            mSubtitle = builder.subtitle();
            mDescription = builder.description();
            mPutIn = new SerializableLatLng(builder.putIn());
            mImageId = builder.imageId();
            mGrade = builder.grade();
            mLength = builder.length();
            mDuration = builder.duration();
        }

        public Section build() {
            return new Section(mId, mTitle, mSubtitle, mDescription, mPutIn, mImageId, mGrade, mLength, mDuration);
        }

        public String description() {
            return mDescription;
        }

        public void description(String description) {
            mDescription = description;
        }

        public String duration() {
            return mDuration;
        }

        public void duration(String duration) {
            mDuration = duration;
        }

        public String grade() {
            return mGrade;
        }

        public void grade(String grade) {
            mGrade = grade;
        }

        public String id() {
            return mId;
        }

        public void id(String id) {
            mId = id;
        }

        public String imageId() {
            return mImageId;
        }

        public void imageId(String imageId) {
            mImageId = imageId;
        }

        public String length() {
            return mLength;
        }

        public void length(String length) {
            mLength = length;
        }

        public LatLng putIn() {
            return mPutIn.getLatLng();
        }

        public void putIn(LatLng putIn) {
            mPutIn = new SerializableLatLng(putIn);
        }

        public String subtitle() {
            return mSubtitle;
        }

        public void subtitle(String subtitle) {
            mSubtitle = subtitle;
        }

        public String title() {
            return mTitle;
        }

        public void title(String title) {
            mTitle = title;
        }
    }
}
