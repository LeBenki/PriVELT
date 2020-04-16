/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.kent.university.privelt.api.PasswordManager
import com.kent.university.privelt.api.ServiceHelper

class PriVELTApplication : Application() {
    var identityManager: PasswordManager? = null
        private set
    var serviceHelper: ServiceHelper? = null
        private set
    var currentActivity: AppCompatActivity? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        serviceHelper = ServiceHelper(this)
        identityManager = PasswordManager()
    }

    companion object {
        @Volatile
        var instance: PriVELTApplication? = null
            private set
    }
}