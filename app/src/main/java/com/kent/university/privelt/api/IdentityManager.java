package com.kent.university.privelt.api;

import android.content.SharedPreferences;

public class IdentityManager {

    private final static String KEY_SALT = "KEY_SALT";
    public final static String SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY";

    private SharedPreferences sharedPreferences;

    private boolean isConnectedToGoogle;

    private boolean isConnectedToStrava;

    private boolean isConnectedToHotelsCom;

    private long mpIndex;

    public IdentityManager(SharedPreferences sharedPreferences) {
        this.isConnectedToGoogle = false;
        this.isConnectedToHotelsCom = false;
        this.isConnectedToStrava = false;
        this.sharedPreferences = sharedPreferences;
        mpIndex = -1;
    }

    public boolean isConnectedToGoogle() {
        return isConnectedToGoogle;
    }

    public void setConnectedToGoogle(boolean connectedToGoogle) {
        isConnectedToGoogle = connectedToGoogle;
    }

    public boolean isConnectedToHotelsCom() {
        return isConnectedToHotelsCom;
    }

    public void setConnectedToHotelsCom(boolean connectedToHotelsCom) {
        isConnectedToHotelsCom = connectedToHotelsCom;
    }

    public boolean isConnectedToStrava() {
        return isConnectedToStrava;
    }

    public void setConnectedToStrava(boolean connectedToStrava) {
        isConnectedToStrava = connectedToStrava;
    }

    public boolean areDataAlreadyPresent() {
        return this.sharedPreferences.contains(KEY_SALT);
    }

    public long getMpIndex() {
        return mpIndex;
    }

    public void setMpIndex(long mpIndex) {
        this.mpIndex = mpIndex;
    }
}
