/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.service

import android.content.Context
import android.content.Intent
import android.os.Build

class ProcessMainClass {
    private fun setServiceIntent(context: Context) {
        if (serviceIntent == null) {
            serviceIntent = Intent(context, Service::class.java)
        }
    }

    /**
     * launching the service
     */
    fun launchService(context: Context?) {
        if (context == null) {
            return
        }
        setServiceIntent(context)
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        private val TAG = ProcessMainClass::class.java.simpleName
        private var serviceIntent: Intent? = null
    }
}