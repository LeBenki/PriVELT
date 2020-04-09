/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sensors;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcManager;

import com.kent.university.privelt.model.Sensor;

import java.util.Objects;

public class SensorManager {
    private static boolean isBluetoothEnabled(Context context) {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) != null && ((BluetoothManager) Objects.requireNonNull(context.getSystemService(Context.BLUETOOTH_SERVICE))).getAdapter() != null && ((BluetoothManager) Objects.requireNonNull(context.getSystemService(Context.BLUETOOTH_SERVICE))).getAdapter().isEnabled();
    }

    private static boolean isWIFIEnabled(Context context) {
       return context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) != null && ((WifiManager) Objects.requireNonNull(context.getApplicationContext().getSystemService(Context.WIFI_SERVICE))).isWifiEnabled();
    }

    private static boolean isNFCEnabled(Context context) {
        return context.getSystemService(Context.NFC_SERVICE) != null && ((NfcManager) Objects.requireNonNull(context.getSystemService(Context.NFC_SERVICE))).getDefaultAdapter() != null && ((NfcManager) Objects.requireNonNull(context.getSystemService(Context.NFC_SERVICE))).getDefaultAdapter().isEnabled();
    }

    private static boolean isLocationEnabled(Context context) {
        return context.getSystemService(Context.LOCATION_SERVICE) != null && ((LocationManager) Objects.requireNonNull(context.getSystemService(Context.LOCATION_SERVICE))).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isEnabled(Sensor sensor, Context context) {
        switch (sensor) {
            case LOCATION:
                return isLocationEnabled(context);
            case BLUETOOTH:
                return isBluetoothEnabled(context);
            case WIFI:
                return isWIFIEnabled(context);
            case NFC:
                return isNFCEnabled(context);
        }
        return false;
    }
}
