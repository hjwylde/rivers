package com.hjwylde.rivers.user.models;

import android.support.annotation.NonNull;

import static java.util.Objects.requireNonNull;

public final class GoogleUser implements User {
    private final String mEmail;

    public GoogleUser(@NonNull String email) {
        this.mEmail = requireNonNull(email);
    }

    @Override
    public String getEmail() {
        return mEmail;
    }

    @Override
    public boolean hasPermission(@NonNull Permission permission) {
        return true;
    }
}