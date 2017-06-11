package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

import com.couchbase.lite.Document;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class SectionDocument implements Serializable {
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

    private static final long serialVersionUID = 1L;

    @NonNull
    private final Map<String, Object> mProperties;

    public SectionDocument(@NonNull Map<String, Object> properties) {
        mProperties = deepClone(properties);

        requireTrue(mProperties.get(BaseDocument.PROPERTY_TYPE).equals(TYPE));

        requireNonNull(mProperties.get(BaseDocument.PROPERTY_ID));
        requireNonNull(mProperties.get(PROPERTY_TITLE));
        requireNonNull(mProperties.get(PROPERTY_PUT_IN));
    }


    private static Map<String, Object> deepClone(Map<String, Object> properties) {
        Map<String, Object> clone = new HashMap<>(properties);
        clone.put(PROPERTY_PUT_IN, new HashMap<>((Map<String, Object>) properties.get(PROPERTY_PUT_IN)));

        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SectionDocument)) {
            return false;
        }

        return getId().equals(((SectionDocument) obj).getId());
    }

    public String getDescription() {
        return (String) mProperties.get(PROPERTY_DESCRIPTION);
    }

    public String getDuration() {
        return (String) mProperties.get(PROPERTY_DURATION);
    }

    public String getGrade() {
        return (String) mProperties.get(PROPERTY_GRADE);
    }

    @NonNull
    public String getId() {
        return (String) mProperties.get(BaseDocument.PROPERTY_ID);
    }

    public String getImageId() {
        return (String) mProperties.get(PROPERTY_IMAGE_ID);
    }

    public String getLength() {
        return (String) mProperties.get(PROPERTY_LENGTH);
    }

    public Map<String, Object> getProperties() {
        return deepClone(mProperties);
    }

    @NonNull
    public LatLng getPutIn() {
        Map<String, Object> putInProperties = (Map<String, Object>) mProperties.get(PROPERTY_PUT_IN);

        return new LatLng((double) putInProperties.get(PROPERTY_PUT_IN_LATITUDE), (double) putInProperties.get(PROPERTY_PUT_IN_LONGITUDE));
    }

    public String getSubtitle() {
        return (String) mProperties.get(PROPERTY_SUBTITLE);
    }

    @NonNull
    public String getTitle() {
        return (String) mProperties.get(PROPERTY_TITLE);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public static final class Builder implements Serializable {
        private static final long serialVersionUID = 1L;

        @NonNull
        private final Map<String, Object> mProperties;

        public Builder() {
            mProperties = new HashMap<>();
            mProperties.put(BaseDocument.PROPERTY_TYPE, TYPE);
        }

        public Builder(@NonNull Map<String, Object> properties) {
            mProperties = deepClone(properties);
        }

        public Builder(@NonNull Document document) {
            this(document.getProperties());
        }

        public Builder(@NonNull SectionDocument section) {
            this(section.mProperties);
        }

        public Builder(@NonNull Builder builder) {
            this(builder.mProperties);
        }

        @NonNull
        public SectionDocument build() {
            return new SectionDocument(mProperties);
        }

        @NonNull
        public SectionDocument.Builder description(String description) {
            mProperties.put(PROPERTY_DESCRIPTION, description);

            return this;
        }

        public String description() {
            return (String) mProperties.get(PROPERTY_DESCRIPTION);
        }

        @NonNull
        public SectionDocument.Builder duration(String duration) {
            mProperties.put(PROPERTY_DURATION, duration);

            return this;
        }

        public String duration() {
            return (String) mProperties.get(PROPERTY_DURATION);
        }

        @NonNull
        public SectionDocument.Builder grade(String grade) {
            mProperties.put(PROPERTY_GRADE, grade);

            return this;
        }

        public String grade() {
            return (String) mProperties.get(PROPERTY_GRADE);
        }

        @NonNull
        public SectionDocument.Builder id(String id) {
            mProperties.put(BaseDocument.PROPERTY_ID, id);

            return this;
        }

        public String imageId() {
            return (String) mProperties.get(PROPERTY_IMAGE_ID);
        }

        @NonNull
        public SectionDocument.Builder imageId(String imageId) {
            mProperties.put(PROPERTY_IMAGE_ID, imageId);

            return this;
        }

        @NonNull
        public SectionDocument.Builder length(String length) {
            mProperties.put(PROPERTY_LENGTH, length);

            return this;
        }

        public String length() {
            return (String) mProperties.get(PROPERTY_LENGTH);
        }

        @NonNull
        public SectionDocument.Builder putIn(LatLng putIn) {
            Map<String, Object> putInProperties = new HashMap<>();
            putInProperties.put(PROPERTY_PUT_IN_LATITUDE, putIn.latitude);
            putInProperties.put(PROPERTY_PUT_IN_LONGITUDE, putIn.longitude);

            mProperties.put(PROPERTY_PUT_IN, putInProperties);

            return this;
        }

        @NonNull
        public SectionDocument.Builder subtitle(String subtitle) {
            mProperties.put(PROPERTY_SUBTITLE, subtitle);

            return this;
        }

        public String subtitle() {
            return (String) mProperties.get(PROPERTY_SUBTITLE);
        }

        @NonNull
        public SectionDocument.Builder title(String title) {
            mProperties.put(PROPERTY_TITLE, title);

            return this;
        }

        public String title() {
            return (String) mProperties.get(PROPERTY_TITLE);
        }
    }
}
