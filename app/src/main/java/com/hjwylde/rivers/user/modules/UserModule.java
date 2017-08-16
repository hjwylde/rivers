package com.hjwylde.rivers.user.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.hjwylde.rivers.R;
import com.hjwylde.rivers.user.models.GoogleUser;
import com.hjwylde.rivers.user.models.UnauthenticatedUser;
import com.hjwylde.rivers.user.models.User;

public final class UserModule {
    private UserModule() {
    }

    @NonNull
    public static User provideUser(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sharedPrefGoogleAccountEmail = context.getString(R.string.pref_key_google_account_email);
        String googleAccountEmail = sharedPreferences.getString(sharedPrefGoogleAccountEmail, null);

        if (googleAccountEmail != null) {
            return new GoogleUser(googleAccountEmail);
        } else {
            return new UnauthenticatedUser();
        }
    }
}