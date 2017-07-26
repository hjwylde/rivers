package com.hjwylde.rivers.ui.activities;

public interface BaseContract {
    interface View {
    }

    interface Presenter {
        void unsubscribe();
    }
}