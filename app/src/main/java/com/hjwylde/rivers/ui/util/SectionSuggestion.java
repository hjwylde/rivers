package com.hjwylde.rivers.ui.util;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.hjwylde.rivers.models.SectionDocument;

import static java.util.Objects.requireNonNull;

public final class SectionSuggestion implements SearchSuggestion {
    public static final Creator<SectionSuggestion> CREATOR = new Creator<SectionSuggestion>() {
        @Override
        public SectionSuggestion createFromParcel(Parcel source) {
            return new SectionSuggestion(source);
        }

        @Override
        public SectionSuggestion[] newArray(int size) {
            return new SectionSuggestion[size];
        }
    };

    private final SectionDocument mSection;

    public SectionSuggestion(@NonNull SectionDocument section) {
        mSection = requireNonNull(section);
    }

    public SectionSuggestion(@NonNull Parcel source) {
        mSection = (SectionDocument) source.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SectionSuggestion)) {
            return false;
        }

        return mSection.equals(((SectionSuggestion) obj).mSection);
    }

    @Override
    public String getBody() {
        StringBuilder body = new StringBuilder();
        body.append(mSection.getTitle());

        if (mSection.getSubtitle() != null && !mSection.getSubtitle().isEmpty()) {
            body.append(", ");
            body.append(mSection.getSubtitle());
        }

        return body.toString();
    }

    @NonNull
    public SectionDocument getSection() {
        return mSection;
    }

    @Override
    public int hashCode() {
        return mSection.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mSection);
    }
}