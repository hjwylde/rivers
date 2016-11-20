package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;

public interface EditSectionContract {
    interface View extends BaseContract.View {
        void onGetImageFailure(@NonNull Throwable t);

        void refreshImage();

        void setImage(@NonNull Image image);
    }

    interface Presenter extends BaseContract.Presenter {
        void getImage(@NonNull String id);
    }
}