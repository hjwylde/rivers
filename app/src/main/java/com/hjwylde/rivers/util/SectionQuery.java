package com.hjwylde.rivers.util;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Section;

import static com.hjwylde.rivers.util.Preconditions.checkNotNull;

public final class SectionQuery {
    private final String mQuery;

    public SectionQuery(@NonNull String query) {
        mQuery = checkNotNull(query);
    }

    public boolean test(Section section) {
        String input = getInput(section).toLowerCase();

        return input.contains(mQuery.toLowerCase());
    }

    private String getInput(Section section) {
        StringBuilder input = new StringBuilder();
        input.append(section.getTitle());

        if (section.getSubtitle() != null) {
            input.append(", ");
            input.append(section.getSubtitle());
        }

        return input.toString();
    }
}
