package com.hjwylde.rivers.ui.util;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.db.models.SectionDocument;

import static java.util.Objects.requireNonNull;

public final class SectionQuery {
    private final String mQuery;

    public SectionQuery(@NonNull String query) {
        mQuery = requireNonNull(query);
    }

    public boolean test(SectionDocument section) {
        String input = getInput(section).toLowerCase();

        return input.contains(mQuery.toLowerCase());
    }

    private String getInput(SectionDocument section) {
        StringBuilder input = new StringBuilder();
        input.append(section.getTitle());

        if (section.getSubtitle() != null) {
            input.append(", ");
            input.append(section.getSubtitle());
        }

        return input.toString();
    }
}
