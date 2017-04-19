package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

public interface CreateSectionContract {
    interface View extends BaseContract.View {
        void onCreateSectionFailure(@NonNull Throwable t);

        void onCreateSectionSuccess(@NonNull Section section);
    }

    interface Presenter extends BaseContract.Presenter {
        void createImage(@NonNull Image image);

        void createSection(@NonNull Section.Builder builder);
    }
}