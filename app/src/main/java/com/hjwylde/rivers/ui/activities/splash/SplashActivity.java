package com.hjwylde.rivers.ui.activities.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;

import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.activities.maps.MapsActivity;

@UiThread
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

        finish();
    }
}