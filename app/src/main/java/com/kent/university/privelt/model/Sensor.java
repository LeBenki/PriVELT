/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.model;

import android.content.Context;
import android.nfc.NfcManager;

import com.kent.university.privelt.R;
import com.kent.university.privelt.utils.sensors.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum Sensor {

    LOCATION("Location",R.drawable.ic_location, true),
    BLUETOOTH("Bluetooth", R.drawable.ic_bluetooth, true),
    STORAGE("Storage", R.drawable.ic_storage, false),
    WIFI("WIFI", R.drawable.ic_wifi, true),
    NFC("NFC", R.drawable.ic_nfc, true),
    CONTACTS("Contacts", R.drawable.ic_contacts, false),
    CALENDAR("Calendar", R.drawable.ic_calendar, false),
    SMS("SMS", R.drawable.ic_sms, false);

    private String name;

    private int resId;

    private List<Application> applications;

    private boolean isSensor;

    Sensor(String name, int resId, boolean isSensor) {
        this.name = name;
        this.resId = resId;
        applications = new ArrayList<>();
        this.isSensor = isSensor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void addApplication(Application application) {
        applications.add(application);
    }

    public boolean isSensor() {
        return isSensor;
    }

    public boolean isEnabled(Context context) {
        return SensorManager.isEnabled(this, context);
    }
}
