/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.model

import android.content.Context
import com.kent.university.privelt.R
import com.kent.university.privelt.utils.sensors.SensorManager
import java.util.*

enum class Sensor(var title: String, val resId: Int, val isSensor: Boolean, val color: Int, val isDangerous: Boolean) {
    LOCATION("Location", R.drawable.ic_location, true, R.color.Location, true),
    BLUETOOTH("Bluetooth", R.drawable.ic_bluetooth, true, R.color.Bluetooth, false),
    STORAGE("Storage", R.drawable.ic_storage, false, R.color.Storage, true),
    WIFI("WIFI", R.drawable.ic_wifi, true, R.color.WIFI, false),
    NFC("NFC", R.drawable.ic_nfc, true, R.color.NFC, false),
    CONTACTS("Contacts", R.drawable.ic_contacts, false, R.color.Contacts, true),
    CALENDAR("Calendar", R.drawable.ic_calendar, false, R.color.Calendar, true),
    SMS("SMS", R.drawable.ic_sms, false, R.color.SMS, true),
    RECORD_AUDIO("Micro", R.drawable.ic_micro, false, R.color.Micro, true),
    GET_ACCOUNTS("Accounts", R.drawable.ic_contacts, false, R.color.Contacts, true),
    CAMERA("Camera", R.drawable.ic_camera, false, R.color.Camera, true),
    READ_PHONE("Phone state", R.drawable.ic_phone, false, R.color.SMS, true),
    MEDIA_CONTENT_CONTROL("Playing content", R.drawable.ic_media, false, R.color.WIFI, false),
    ACTIVITY_RECOGNITION("Activity recognition", R.drawable.ic_sport, false, R.color.Calendar, true),
    NETWORK_STATE("Network state", R.drawable.ic_wifi, false, R.color.Bluetooth, false),
    BODY_SENSORS("Body sensors", R.drawable.ic_heart, false, R.color.Location, true);

    private val applications: MutableList<Application>

    fun getApplications(): MutableList<Application> {
        return applications
    }

    fun addApplication(application: Application) {
        applications.add(application)
    }

    fun isEnabled(context: Context?): Boolean {
        return SensorManager.isEnabled(this, context!!)
    }

    init {
        applications = ArrayList()
    }
}