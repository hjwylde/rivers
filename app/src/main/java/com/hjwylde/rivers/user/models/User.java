package com.hjwylde.rivers.user.models;

import android.support.annotation.NonNull;

public interface User {
    String getEmail();

    boolean hasPermission(@NonNull Permission permission);
}