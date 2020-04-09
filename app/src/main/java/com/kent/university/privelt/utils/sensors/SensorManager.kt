/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils.sensors

import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.nfc.NfcManager
import com.kent.university.privelt.model.Sensor
import java.util.*

object SensorManager {
    private fun isBluetoothEnabled(context: Context): Boolean {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) != null && (Objects.requireNonNull(context.getSystemService(Context.BLUETOOTH_SERVICE)) as BluetoothManager).adapter.isEnabled
    }

    private fun isWIFIEnabled(context: Context): Boolean {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) != null && (Objects.requireNonNull(context.applicationContext.getSystemService(Context.WIFI_SERVICE)) as WifiManager).isWifiEnabled
    }

    private fun isNFCEnabled(context: Context): Boolean {
        return context.getSystemService(Context.NFC_SERVICE) != null && (Objects.requireNonNull(context.getSystemService(Context.NFC_SERVICE)) as NfcManager).defaultAdapter.isEnabled
    }

    private fun isLocationEnabled(context: Context): Boolean {
        return context.getSystemService(Context.LOCATION_SERVICE) != null && (Objects.requireNonNull(context.getSystemService(Context.LOCATION_SERVICE)) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun isEnabled(sensor: Sensor?, context: Context): Boolean {
        return when (sensor) {
            Sensor.LOCATION -> isLocationEnabled(context)
            Sensor.BLUETOOTH -> isBluetoothEnabled(context)
            Sensor.WIFI -> isWIFIEnabled(context)
            Sensor.NFC -> isNFCEnabled(context)
            else -> false
        }
    }
}