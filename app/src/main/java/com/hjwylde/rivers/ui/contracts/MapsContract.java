package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.models.Section;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void clearSelection();

        void createSection(@NonNull LatLng putIn);

        void onGetSectionSuggestionsFailure(@NonNull Throwable t);

        void refreshMap();

        void selectSection(@NonNull String id);

        void setSectionSuggestions(@NonNull List<SectionSuggestion> sectionSuggestions);

        void setSections(@NonNull List<? extends Section> sections);
    }

    interface Presenter extends BaseContract.Presenter {
        void getSectionSuggestions(@NonNull String query);

        void getSections();
    }
}