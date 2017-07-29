package com.hjwylde.rivers.ui.activities.home;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Section;

import static java.util.Objects.requireNonNull;

public final class SectionQuery {
    private final String mQuery;

    public SectionQuery(@NonNull String query) {
        mQuery = requireNonNull(query);
    }

    public boolean test(Section section) {
        String input = getInput(section).toLowerCase();

        return input.contains(mQuery.toLowerCase());
    }

    private String getInput(Section section) {
        return section.getTitle() + ", " + section.getSubtitle();
    }
}
