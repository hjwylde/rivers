package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.db.models.ImageDocument;
import com.hjwylde.rivers.db.models.SectionDocument;
import com.hjwylde.rivers.ui.util.SectionSuggestion;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void clearSelection();

        void createSection(@NonNull LatLng putIn);

        void onDeleteSectionFailure(@NonNull Throwable t);

        void onDeleteSectionSuccess();

        void onGetImageFailure(@NonNull Throwable t);

        void onGetSectionSuggestionsFailure(@NonNull Throwable t);

        void refreshImage();

        void refreshMap();

        void selectSection(@NonNull SectionDocument section);

        void setImage(@NonNull ImageDocument image);

        void setSectionSuggestions(@NonNull List<SectionSuggestion> sectionSuggestions);

        void setSections(@NonNull List<SectionDocument> sections);
    }

    interface Presenter extends BaseContract.Presenter {
        void deleteSection(@NonNull SectionDocument section);

        void getImage(@NonNull String id);

        void getSectionSuggestions(@NonNull String query);

        void streamSections();
    }
}