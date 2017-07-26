package com.hjwylde.rivers.ui.activities.licenses;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hjwylde.rivers.ui.activities.BaseActivity;

public class LicensesActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new LicensesFragment())
                .commit();
    }
}
