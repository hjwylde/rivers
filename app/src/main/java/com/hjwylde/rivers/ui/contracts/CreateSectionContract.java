package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.ImageDocument;
import com.hjwylde.rivers.models.Section;

public interface CreateSectionContract {
    interface View extends BaseContract.View {
        void onCreateImageFailure(@NonNull Throwable t);

        void onCreateImageSuccess(@NonNull ImageDocument image);

        void onCreateSectionFailure(@NonNull Throwable t);

        void onCreateSectionSuccess(@NonNull Section section);

        void onGetImageFailure(@NonNull Throwable t);

        void refreshImage();

        void setImage(@NonNull ImageDocument image);
    }

    interface Presenter extends BaseContract.Presenter {
        void createImage(@NonNull ImageDocument.Builder image);

        void createSection(@NonNull Section.Builder builder);

        void getImage(@NonNull String id);
    }
}