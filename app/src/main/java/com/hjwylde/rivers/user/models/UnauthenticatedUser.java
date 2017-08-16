package com.hjwylde.rivers.user.models;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public final class UnauthenticatedUser implements User {
    private static final List<Permission> WHITELISTED_PERMISSIONS = Arrays.asList(
            Permission.Image.READ,
            Permission.Section.READ
    );

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public boolean hasPermission(@NonNull Permission permission) {
        return WHITELISTED_PERMISSIONS.contains(permission);
    }
}