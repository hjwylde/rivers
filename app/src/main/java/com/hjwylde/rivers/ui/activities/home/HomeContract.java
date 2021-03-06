package com.hjwylde.rivers.ui.activities.home;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.ui.activities.BaseContract;

import java.util.List;

public interface HomeContract {
    interface View extends BaseContract.View {
        void createSection(@NonNull LatLng putIn);

        void onGetSectionSuggestionsFailure(@NonNull Throwable t);

        void selectSection(@NonNull String id);

        void setSectionSuggestions(@NonNull List<SectionSuggestion> sectionSuggestions);
    }

    interface Presenter extends BaseContract.Presenter {
        void getSectionSuggestions(@NonNull String query);
    }
}