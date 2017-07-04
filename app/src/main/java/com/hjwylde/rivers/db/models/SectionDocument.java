package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.models.Section;

import java.util.HashMap;
import java.util.Map;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class SectionDocument extends BaseDocument implements Section {
    @NonNull
    public static final String TYPE = "section";

    @NonNull
    public static final String PROPERTY_TITLE = "title";
    @NonNull
    public static final String PROPERTY_SUBTITLE = "subtitle";
    @NonNull
    public static final String PROPERTY_DESCRIPTION = "description";
    @NonNull
    public static final String PROPERTY_PUT_IN = "putIn";
    @NonNull
    public static final String PROPERTY_PUT_IN_LATITUDE = "latitude";
    @NonNull
    public static final String PROPERTY_PUT_IN_LONGITUDE = "longitude";
    @NonNull
    public static final String PROPERTY_IMAGE_ID = "imageId";
    @NonNull
    public static final String PROPERTY_GRADE = "grade";
    @NonNull
    public static final String PROPERTY_LENGTH = "length";
    @NonNull
    public static final String PROPERTY_DURATION = "duration";

    public SectionDocument(@NonNull Document document) {
        super(document);

        requireTrue(TYPE.equals(getType()));
    }

    public static Builder builder(@NonNull Database database) {
        return new Builder(database);
    }

    @Override
    public String getDescription() {
        return (String) mDocument.getProperty(PROPERTY_DESCRIPTION);
    }

    @Override
    public String getDuration() {
        return (String) mDocument.getProperty(PROPERTY_DURATION);
    }

    @Override
    public String getGrade() {
        return (String) mDocument.getProperty(PROPERTY_GRADE);
    }

    @NonNull
    @Override
    public String getId() {
        return (String) mDocument.getProperty(BaseDocument.PROPERTY_ID);
    }

    @Override
    public String getImageId() {
        return (String) mDocument.getProperty(PROPERTY_IMAGE_ID);
    }

    @Override
    public String getLength() {
        return (String) mDocument.getProperty(PROPERTY_LENGTH);
    }

    @NonNull
    @Override
    public LatLng getPutIn() {
        Map<String, Object> putInProperties = (Map<String, Object>) mDocument.getProperty(PROPERTY_PUT_IN);
        double latitude = (double) putInProperties.get(PROPERTY_PUT_IN_LATITUDE);
        double longitude = (double) putInProperties.get(PROPERTY_PUT_IN_LONGITUDE);

        return new LatLng(latitude, longitude);
    }

    @Override
    public String getSubtitle() {
        return (String) mDocument.getProperty(PROPERTY_SUBTITLE);
    }

    @NonNull
    @Override
    public String getTitle() {
        return (String) mDocument.getProperty(PROPERTY_TITLE);
    }

    public static final class Builder extends BaseDocument.Builder implements Section.Builder {
        private Builder(@NonNull Database database) {
            super(database, TYPE);
        }

        public Builder copy(@NonNull Section.Builder builder) {
            id(builder.id());
            title(builder.title());
            subtitle(builder.subtitle());
            description(builder.description());
            putIn(builder.putIn());
            imageId(builder.imageId());
            grade(builder.grade());
            length(builder.length());
            duration(builder.duration());

            return this;
        }

        @NonNull
        @Override
        public SectionDocument create() throws CouchbaseLiteException {
            return new SectionDocument(createDocument());
        }

        @NonNull
        @Override
        public Builder description(String description) {
            mProperties.put(PROPERTY_DESCRIPTION, description);

            return this;
        }

        @Override
        public String description() {
            return (String) mProperties.get(PROPERTY_DESCRIPTION);
        }

        @NonNull
        @Override
        public Builder duration(String duration) {
            mProperties.put(PROPERTY_DURATION, duration);

            return this;
        }

        @Override
        public String duration() {
            return (String) mProperties.get(PROPERTY_DURATION);
        }

        @NonNull
        @Override
        public Builder grade(String grade) {
            mProperties.put(PROPERTY_GRADE, grade);

            return this;
        }

        @Override
        public String grade() {
            return (String) mProperties.get(PROPERTY_GRADE);
        }

        @NonNull
        @Override
        public Builder id(String id) {
            mProperties.put(BaseDocument.PROPERTY_ID, id);

            return this;
        }

        @Override
        public String imageId() {
            return (String) mProperties.get(PROPERTY_IMAGE_ID);
        }

        @NonNull
        @Override
        public Builder imageId(String imageId) {
            mProperties.put(PROPERTY_IMAGE_ID, imageId);

            return this;
        }

        @NonNull
        @Override
        public Builder length(String length) {
            mProperties.put(PROPERTY_LENGTH, length);

            return this;
        }

        @Override
        public String length() {
            return (String) mProperties.get(PROPERTY_LENGTH);
        }

        @Override
        public LatLng putIn() {
            if (mProperties.containsKey(PROPERTY_PUT_IN)) {
                Map<String, Object> putInProperties = (Map<String, Object>) mProperties.get(PROPERTY_PUT_IN);
                double latitude = (double) putInProperties.get(PROPERTY_PUT_IN_LATITUDE);
                double longitude = (double) putInProperties.get(PROPERTY_PUT_IN_LONGITUDE);

                return new LatLng(latitude, longitude);
            }

            return null;
        }

        @NonNull
        @Override
        public Builder putIn(LatLng putIn) {
            if (putIn != null) {
                Map<String, Object> putInProperties = new HashMap<>();
                putInProperties.put(PROPERTY_PUT_IN_LATITUDE, putIn.latitude);
                putInProperties.put(PROPERTY_PUT_IN_LONGITUDE, putIn.longitude);

                mProperties.put(PROPERTY_PUT_IN, putInProperties);
            }

            return this;
        }

        @NonNull
        @Override
        public Builder subtitle(String subtitle) {
            mProperties.put(PROPERTY_SUBTITLE, subtitle);

            return this;
        }

        @Override
        public String subtitle() {
            return (String) mProperties.get(PROPERTY_SUBTITLE);
        }

        @NonNull
        @Override
        public Builder title(String title) {
            mProperties.put(PROPERTY_TITLE, title);

            return this;
        }

        @Override
        public String title() {
            return (String) mProperties.get(PROPERTY_TITLE);
        }

        @NonNull
        @Override
        public SectionDocument update() throws CouchbaseLiteException {
            return new SectionDocument(updateDocument());
        }

        @Override
        protected void validate() {
            requireTrue(TYPE.equals(type()));

            requireNonNull(title());
            requireNonNull(subtitle());
            requireNonNull(putIn());
        }
    }
}
