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

enum class Sensor(var title: String, val resId: Int, val isSensor: Boolean, val color: Int) {
    LOCATION("Location", R.drawable.ic_location, true, R.color.Location),
    BLUETOOTH("Bluetooth", R.drawable.ic_bluetooth, true, R.color.Bluetooth),
    STORAGE("Storage", R.drawable.ic_storage, false, R.color.Storage),
    WIFI("WIFI", R.drawable.ic_wifi, true, R.color.WIFI),
    NFC("NFC", R.drawable.ic_nfc, true, R.color.NFC),
    CONTACTS("Contacts", R.drawable.ic_contacts, false, R.color.Contacts),
    CALENDAR("Calendar", R.drawable.ic_calendar, false, R.color.Calendar),
    SMS("SMS", R.drawable.ic_sms, false, R.color.SMS);

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