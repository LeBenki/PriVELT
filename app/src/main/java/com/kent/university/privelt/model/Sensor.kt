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

enum class Sensor(var title: String, val resId: Int, val isSensor: Boolean) {
    LOCATION("Location", R.drawable.ic_location, true),
    BLUETOOTH("Bluetooth", R.drawable.ic_bluetooth, true),
    STORAGE("Storage", R.drawable.ic_storage, false),
    WIFI("WIFI", R.drawable.ic_wifi, true),
    NFC("NFC", R.drawable.ic_nfc, true),
    CONTACTS("Contacts", R.drawable.ic_contacts, false),
    CALENDAR("Calendar", R.drawable.ic_calendar, false),
    SMS("SMS", R.drawable.ic_sms, false);

    private val applications: MutableList<Application>

    fun getApplications(): List<Application> {
        return applications
    }

    fun addApplication(application: Application) {
        applications.add(application)
    }

    fun isEnabled(context: Context?): Boolean {
        return SensorManager.isEnabled(this, context)
    }

    init {
        applications = ArrayList()
    }
}