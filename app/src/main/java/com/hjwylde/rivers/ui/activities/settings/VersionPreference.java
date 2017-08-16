package com.hjwylde.rivers.ui.activities.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.hjwylde.rivers.BuildConfig;
import com.hjwylde.rivers.R;

public final class VersionPreference extends Preference {
    {
        setPersistent(false);
        setSummary(BuildConfig.VERSION_NAME);
        setTitle(R.string.pref_title_version);
    }

    public VersionPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public VersionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VersionPreference(Context context) {
        super(context);
    }
}