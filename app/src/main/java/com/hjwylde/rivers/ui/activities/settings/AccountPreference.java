package com.hjwylde.rivers.ui.activities.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.hjwylde.rivers.R;

public final class AccountPreference extends Preference {
    {
        setPersistent(false);
        setTitle(R.string.pref_title_account_not_signed_in);
    }

    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initTitle();
    }

    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTitle();
    }

    public AccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        initTitle();
    }

    public AccountPreference(Context context) {
        super(context);

        initTitle();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);
    }

    private void initTitle() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sharedPrefGoogleAccountEmail = getContext().getString(R.string.pref_key_google_account_email);
        String googleAccountEmail = sharedPreferences.getString(sharedPrefGoogleAccountEmail, null);

        if (googleAccountEmail != null) {
            setTitle(googleAccountEmail);
        }
    }
}