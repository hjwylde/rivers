package com.hjwylde.rivers.ui.contracts;

public interface BaseContract {
    interface View {
    }

    interface Presenter {
        void unsubscribe();
    }
}
