package com.hjwylde.rivers.ui.activities;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    @NonNull
    protected EditText findEditTextById(@IdRes int id) {
        return findTById(id);
    }

    @NonNull
    protected <T extends View> T findTById(@IdRes int id) {
        return (T) findViewById(id);
    }

    @NonNull
    protected TextView findTextViewById(@IdRes int id) {
        return findTById(id);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}