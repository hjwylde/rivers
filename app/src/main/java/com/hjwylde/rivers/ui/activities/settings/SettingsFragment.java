package com.hjwylde.rivers.ui.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.licenses.LicensesActivity;

@UiThread
public final class SettingsFragment extends PreferenceFragment {
    private Preference mLicensesPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_preferences);

        bindPreferences();

        initOnClickListeners();
    }

    private void bindPreferences() {
        mLicensesPreference = findPreference(getString(R.string.dummy_pref_key_licenses));
    }

    private void initOnClickListeners() {
        mLicensesPreference.setOnPreferenceClickListener(this::onLicensesPreferenceClick);
    }

    private boolean onLicensesPreferenceClick(@NonNull Preference preference) {
        Intent intent = new Intent(getActivity(), LicensesActivity.class);
        startActivity(intent);

        return true;
    }
}