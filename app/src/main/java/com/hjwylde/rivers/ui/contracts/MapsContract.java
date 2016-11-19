package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void onCreateSectionClick();

        void onGetImageFailure(@NonNull Throwable t);

        void onGetSectionsFailure(@NonNull Throwable t);

        void onMapClick();

        void onSectionClick(@NonNull Section section);

        void refreshImage();

        void refreshMap();

        void setImage(@NonNull Image image);

        void setSections(@NonNull List<Section> sections);
    }

    interface Presenter extends BaseContract.Presenter {
        void getImage(@NonNull String id);

        void getSections();
    }
}