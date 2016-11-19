package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

import com.hjwylde.rivers.models.Action;

public interface CreateSectionContract {
    interface View extends BaseContract.View {
        void onCreateSectionFailure(@NonNull Throwable t);

        void onCreateSectionSuccess(@NonNull Action action);
    }

    interface Presenter extends BaseContract.Presenter {
        void createSection(@NonNull Action action);
    }
}