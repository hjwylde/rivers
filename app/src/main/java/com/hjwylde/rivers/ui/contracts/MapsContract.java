package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void setSections(@NonNull List<Section> sections);

        void refreshMap();

        void onGetSectionsFailure(@NonNull Throwable t);

        void onSectionClick(@NonNull Section section);

        void onCreateSectionClick();

        void setImage(@NonNull Image image);

        void refreshImage();

        void onGetImageFailure(@NonNull Throwable t);

        void onMapClick();
    }

    interface Presenter extends BaseContract.Presenter {
        void getSections();

        void getImage(@NonNull String id);
    }
}