package com.kent.university.privelt.utils;

import com.kent.university.privelt.R;

public enum UserDataType {

    LOCATION("location", R.drawable.ic_pin),
    DEVICE("device", R.drawable.ic_cellphone),
    ADDRESS("address", R.drawable.ic_pin),
    PROFILE("profile", R.drawable.ic_user),
    BOOKING("booking", R.drawable.ic_sleep),
    PASSPORT("passport", R.drawable.ic_user),
    CB("cb", R.drawable.ic_visa);

    private String type;

    private int res;

    UserDataType(String type, int res) {
        this.type = type;
        this.res = res;
    }

    public String getType() {
        return type;
    }

    public int getRes() {
        return res;
    }
}
