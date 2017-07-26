package com.hjwylde.rivers.ui.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hjwylde.rivers.ui.activities.BaseActivity;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}