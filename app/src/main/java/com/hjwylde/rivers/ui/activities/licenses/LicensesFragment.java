package com.hjwylde.rivers.ui.activities.licenses;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.hjwylde.rivers.R;

@UiThread
public final class LicensesFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.license_preferences);
    }
}
