package com.hjwylde.rivers.ui.activities;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    @NonNull
    public ImageView findImageViewById(@IdRes int id) {
        return findTById(id);
    }

    @NonNull
    public <T extends View> T findTById(@IdRes int id) {
        return (T) findViewById(id);
    }

    @NonNull
    public TextView findTextViewById(@IdRes int id) {
        return findTById(id);
    }

    @NonNull
    public Toolbar findToolbarById(@IdRes int id) {
        return findTById(id);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}