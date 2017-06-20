package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Section {
    @NonNull
    static DefaultBuilder builder() {
        return new DefaultBuilder();
    }

    String getDescription();

    String getDuration();

    String getGrade();

    @NonNull
    String getId();

    String getImageId();

    String getLength();

    @NonNull
    LatLng getPutIn();

    String getSubtitle();

    @NonNull
    String getTitle();

    interface Builder {
        @NonNull
        Section build() throws Exception;

        String description();

        @NonNull
        Builder description(String description);

        String duration();

        @NonNull
        Builder duration(String duration);

        String grade();

        @NonNull
        Builder grade(String grade);

        String id();

        @NonNull
        Builder id(String id);

        String imageId();

        @NonNull
        Builder imageId(String imageId);

        String length();

        @NonNull
        Builder length(String length);

        LatLng putIn();

        @NonNull
        Builder putIn(LatLng putIn);

        String subtitle();

        @NonNull
        Builder subtitle(String subtitle);

        String title();

        @NonNull
        Builder title(String title);
    }

    final class DefaultBuilder implements Builder, Serializable {
        private static final long serialVersionUID = 1L;

        private String mId;
        private String mTitle;
        private String mSubtitle;
        private String mDescription;
        private LatLng mPutIn;
        private String mImageId;
        private String mGrade;
        private String mLength;
        private String mDuration;

        private DefaultBuilder() {
        }

        @NonNull
        @Override
        public Section build() {
            throw new UnsupportedOperationException();
        }

        public DefaultBuilder copy(@NonNull Section section) {
            id(section.getId());
            subtitle(section.getSubtitle());
            description(section.getDescription());
            putIn(section.getPutIn());
            imageId(section.getImageId());
            grade(section.getGrade());
            length(section.getLength());
            duration(section.getDuration());

            return this;
        }

        @Override
        public String description() {
            return mDescription;
        }

        @NonNull
        @Override
        public DefaultBuilder description(String description) {
            mDescription = description;

            return this;
        }

        @Override
        public String duration() {
            return mDuration;
        }

        @NonNull
        @Override
        public DefaultBuilder duration(String duration) {
            mDuration = duration;

            return this;
        }

        @Override
        public String grade() {
            return mGrade;
        }

        @NonNull
        @Override
        public DefaultBuilder grade(String grade) {
            mGrade = grade;

            return this;
        }

        @Override
        public String id() {
            return mId;
        }

        @NonNull
        @Override
        public DefaultBuilder id(String id) {
            mId = id;

            return this;
        }

        @Override
        public String imageId() {
            return mImageId;
        }

        @NonNull
        @Override
        public DefaultBuilder imageId(String imageId) {
            mImageId = imageId;

            return this;
        }

        @Override
        public String length() {
            return mLength;
        }

        @NonNull
        @Override
        public DefaultBuilder length(String length) {
            mLength = length;

            return this;
        }

        @Override
        public LatLng putIn() {
            return mPutIn;
        }

        @NonNull
        @Override
        public DefaultBuilder putIn(LatLng putIn) {
            mPutIn = putIn;

            return this;
        }

        @Override
        public String subtitle() {
            return mSubtitle;
        }

        @NonNull
        @Override
        public DefaultBuilder subtitle(String subtitle) {
            mSubtitle = subtitle;

            return this;
        }

        @Override
        public String title() {
            return mTitle;
        }

        @NonNull
        @Override
        public DefaultBuilder title(String title) {
            mTitle = title;

            return this;
        }

        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            mId = (String) in.readObject();
            mTitle = (String) in.readObject();
            mSubtitle = (String) in.readObject();
            mDescription = (String) in.readObject();
            if (in.readBoolean()) {
                mPutIn = new LatLng(in.readDouble(), in.readDouble());
            }
            mImageId = (String) in.readObject();
            mGrade = (String) in.readObject();
            mLength = (String) in.readObject();
            mDuration = (String) in.readObject();
        }

        private void writeObject(ObjectOutputStream out)
                throws IOException {
            out.writeObject(mId);
            out.writeObject(mTitle);
            out.writeObject(mSubtitle);
            out.writeObject(mDescription);
            if (mPutIn != null) {
                out.writeBoolean(true);
                out.writeDouble(mPutIn.latitude);
                out.writeDouble(mPutIn.longitude);
            } else {
                out.writeBoolean(false);
            }
            out.writeObject(mImageId);
            out.writeObject(mGrade);
            out.writeObject(mLength);
            out.writeObject(mDuration);
        }
    }
}
