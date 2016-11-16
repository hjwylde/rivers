package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Section;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void setSections(@NonNull List<Section> sections);

        void refreshMap();

        void onGetSectionsFailure(@NonNull Throwable t);

        void startSectionActivity(@NonNull Section section);

        void onCreateSectionClick();
    }

    interface Presenter extends BaseContract.Presenter {
        void getSections();
    }
}