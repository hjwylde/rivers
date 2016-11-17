package com.hjwylde.rivers.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected TextView findTextViewById(int id) {
        return (TextView) findViewById(id);
    }

    protected Toolbar findToolbarById(int id) {
        return (Toolbar) findViewById(id);
    }
}