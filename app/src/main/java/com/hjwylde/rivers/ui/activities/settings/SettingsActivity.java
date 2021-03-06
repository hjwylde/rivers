package com.hjwylde.rivers.ui.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.hjwylde.rivers.ui.activities.BaseActivity;

@UiThread
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