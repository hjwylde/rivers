package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;
import com.hjwylde.rivers.models.Image;

public interface EditSectionContract {
    interface View extends BaseContract.View {
        void setImage(@NonNull Image image);

        void refreshImage();

        void onGetImageFailure(@NonNull Throwable t);

        void onUpdateSectionSuccess(@NonNull Action action);

        void onUpdateSectionFailure(@NonNull Throwable t);
    }

    interface Presenter extends BaseContract.Presenter {
        void updateSection(@NonNull Action action);

        void getImage(@NonNull String id);
    }
}