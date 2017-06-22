package com.hjwylde.rivers.ui.activities;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.content.Context;

import com.hjwylde.v7.app.AppCompatPreferenceActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BasePreferenceActivity extends AppCompatPreferenceActivity implements LifecycleRegistryOwner {
    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @Override
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}