package com.hjwylde.rivers.db.models;

import android.support.annotation.NonNull;

import com.couchbase.lite.Document;
import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.models.AbstractSection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.hjwylde.rivers.util.Preconditions.requireTrue;
import static java.util.Objects.requireNonNull;

public final class SectionDocument extends AbstractSection {
    @NonNull
    public static final String TYPE = "section";

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
    public String getDescription() {
        return (String) mProperties.get(PROPERTY_DESCRIPTION);
    }

    @Override
    public String getDuration() {
        return (String) mProperties.get(PROPERTY_DURATION);
    }

    @Override
    public String getGrade() {
        return (String) mProperties.get(PROPERTY_GRADE);
    }

    @NonNull
    @Override
    public String getId() {
        return (String) mProperties.get(BaseDocument.PROPERTY_ID);
    }

    @Override
    public String getImageId() {
        return (String) mProperties.get(PROPERTY_IMAGE_ID);
    }

    @Override
    public String getLength() {
        return (String) mProperties.get(PROPERTY_LENGTH);
    }

    public Map<String, Object> getProperties() {
        return deepClone(mProperties);
    }

    @NonNull
    @Override
    public LatLng getPutIn() {
        Map<String, Object> putInProperties = (Map<String, Object>) mProperties.get(PROPERTY_PUT_IN);

        return new LatLng((double) putInProperties.get(PROPERTY_PUT_IN_LATITUDE), (double) putInProperties.get(PROPERTY_PUT_IN_LONGITUDE));
    }

    @Override
    public String getSubtitle() {
        return (String) mProperties.get(PROPERTY_SUBTITLE);
    }

    @NonNull
    @Override
    public String getTitle() {
        return (String) mProperties.get(PROPERTY_TITLE);
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