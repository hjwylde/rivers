package com.hjwylde.rivers.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

// TODO (hjw): remove this activity and use preference headers or fragments properly
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