package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class Section implements Serializable {
    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_SUBTITLE = "subtitle";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_PUT_IN = "putIn";
    public static final String PROPERTY_IMAGE_ID = "imageId";
    public static final String PROPERTY_GRADE = "grade";
    public static final String PROPERTY_LENGTH = "length";
    public static final String PROPERTY_DURATION = "duration";

    private static final long serialVersionUID = 1L;

    @SerializedName(PROPERTY_ID)
    private final String mId;
    @SerializedName(PROPERTY_TITLE)
    private final String mTitle;
    @SerializedName(PROPERTY_SUBTITLE)
    private final String mSubtitle;
    @SerializedName(PROPERTY_DESCRIPTION)
    private final String mDescription;
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

    public static String getCollection() {
        return Action.COLLECTION_SECTIONS;
    }

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

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
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

        public Builder(Section section) {
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

        public Builder(Builder builder) {
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

        public String id() {
            return mId;
        }

        public String title() {
            return mTitle;
        }

        public String subtitle() {
            return mSubtitle;
        }

        public String description() {
            return mDescription;
        }

        public LatLng putIn() {
            return mPutIn.getLatLng();
        }

        public String imageId() {
            return mImageId;
        }

        public String grade() {
            return mGrade;
        }

        public String length() {
            return mLength;
        }

        public String duration() {
            return mDuration;
        }

        public void id(String id) {
            mId = id;
        }

        public void title(String title) {
            mTitle = title;
        }

        public void subtitle(String subtitle) {
            mSubtitle = subtitle;
        }

        public void description(String description) {
            mDescription = description;
        }

        public void putIn(LatLng putIn) {
            mPutIn = new SerializableLatLng(putIn);
        }

        public void imageId(String imageId) {
            mImageId = imageId;
        }

        public void grade(String grade) {
            mGrade = grade;
        }

        public void length(String length) {
            mLength = length;
        }

        public void duration(String duration) {
            mDuration = duration;
        }
    }
}
