package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

import java.util.List;

public interface MapsContract {
    interface View extends BaseContract.View {
        void clearSelection();

        void createSection(@NonNull LatLng putIn);

        void onDeleteSectionFailure(@NonNull Throwable t);

        void onDeleteSectionSuccess();

        void onGetImageFailure(@NonNull Throwable t);

        void refreshImage();

        void refreshMap();

        void selectSection(@NonNull Section section);

        void setImage(@NonNull Image image);

        void setSections(@NonNull List<Section> sections);
    }

    interface Presenter extends BaseContract.Presenter {
        void deleteSection(@NonNull Section section);

        void getImage(@NonNull String id);

        void streamSections();
    }
}