package com.hjwylde.rivers.ui.contracts;

import android.support.annotation.NonNull;

public interface CreateSectionContract {
    interface View extends BaseContract.View {
        void onCreateSectionFailure(@NonNull Throwable t);

        void onCreateSectionSuccess();
    }

    interface Presenter extends BaseContract.Presenter {
        void createSection();
    }
}