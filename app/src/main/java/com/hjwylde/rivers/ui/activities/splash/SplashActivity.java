package com.hjwylde.rivers.ui.activities.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.ui.activities.BaseActivity;
import com.hjwylde.rivers.ui.activities.home.HomeActivity;
import com.hjwylde.rivers.ui.activities.signIn.SignInActivity;

import butterknife.BindString;
import butterknife.ButterKnife;

@UiThread
public class SplashActivity extends BaseActivity {
    @BindString(R.string.pref_key_is_first_launch)
    String mSharedPrefIsFirstLaunch;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Class<? extends Activity> clazz = isFirstLaunch() ? SignInActivity.class : HomeActivity.class;
        Intent intent = new Intent(this, clazz);
        startActivity(intent);

        finish();
    }

    private boolean isFirstLaunch() {
        return mSharedPreferences.getBoolean(mSharedPrefIsFirstLaunch, true);
    }
}