package com.hjwylde.rivers.ui.activities.home;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.hjwylde.rivers.models.Section;

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

    @NonNull
    private final String mId;
    @NonNull
    private final String mTitle;
    @NonNull
    private final String mSubtitle;

    public SectionSuggestion(@NonNull Section section) {
        mId = section.getId();
        mTitle = section.getTitle();
        mSubtitle = section.getSubtitle();
    }

    private SectionSuggestion(@NonNull Parcel source) {
        mId = source.readString();
        mTitle = source.readString();
        mSubtitle = source.readString();
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

        return mId.equals(((SectionSuggestion) obj).mId);
    }

    @Override
    public String getBody() {
        return mTitle + ", " + mSubtitle;
    }

    @NonNull
    public String getSectionId() {
        return mId;
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mSubtitle);
    }
}