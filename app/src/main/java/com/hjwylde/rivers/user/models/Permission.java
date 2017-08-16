package com.hjwylde.rivers.user.models;

public interface Permission {
    enum Section implements Permission {
        CREATE, DELETE, READ, UPDATE;
    }

    enum Image implements Permission {
        CREATE, DELETE, READ, UPDATE;
    }
}