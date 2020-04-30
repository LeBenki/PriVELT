/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.kent.university.privelt.api.PasswordManager
import com.kent.university.privelt.api.ServiceHelper
import com.kent.university.privelt.worker.PermissionsWorker
import java.util.concurrent.TimeUnit

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

        val myWorkBuilder = PeriodicWorkRequest.Builder(PermissionsWorker::class.java, 1, TimeUnit.HOURS)

        val myWork = myWorkBuilder.build()
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("PermissionsWorker", ExistingPeriodicWorkPolicy.REPLACE, myWork)
    }

    companion object {
        @Volatile
        var instance: PriVELTApplication? = null
            private set
    }
}