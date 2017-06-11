package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.ImageDocument;
import com.hjwylde.rivers.models.SectionDocument;

public interface EditSectionContract {
    interface View extends BaseContract.View {
        void onCreateImageFailure(@NonNull Throwable t);

        void onCreateImageSuccess(@NonNull ImageDocument image);

        void onGetImageFailure(@NonNull Throwable t);

        void onUpdateSectionFailure(@NonNull Throwable t);

        void onUpdateSectionSuccess(@NonNull SectionDocument section);

        void refreshImage();

        void setImage(@NonNull ImageDocument image);
    }

    interface Presenter extends BaseContract.Presenter {
        void createImage(@NonNull ImageDocument.Builder image);

        void getImage(@NonNull String id);

        void updateSection(@NonNull SectionDocument.Builder builder);
    }
}