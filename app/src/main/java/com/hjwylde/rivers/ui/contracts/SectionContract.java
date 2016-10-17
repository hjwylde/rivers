package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;

public interface SectionContract {
    interface View extends BaseContract.View {
        void setImage(@NonNull Image image);

        void refreshImage();

        void onGetImageFailure(@NonNull Throwable t);

        void onDeleteSectionSuccess(@NonNull Action action);

        void onDeleteSectionFailure(@NonNull Throwable t);
    }

    interface Presenter extends BaseContract.Presenter {
        void deleteSection(@NonNull Action action);

        void getImage(@NonNull String id);
    }
}