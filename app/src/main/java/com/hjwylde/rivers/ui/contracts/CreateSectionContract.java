package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Image;
import com.hjwylde.rivers.models.Section;

public interface CreateSectionContract {
    interface View extends BaseContract.View {
        void onCreateSectionSuccess(@NonNull Section section);

        void onCreateSectionFailure(@NonNull Throwable t);
    }

    interface Presenter extends BaseContract.Presenter {
        void createSection(@NonNull Section.Builder builder);

        void createImage(@NonNull Image image);
    }
}