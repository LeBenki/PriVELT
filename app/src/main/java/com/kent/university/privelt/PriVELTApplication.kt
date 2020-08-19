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
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.utils.ontology.OntologyBuilder
import com.kent.university.privelt.worker.PermissionsWorker
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PriVELTApplication : Application() {
    var identityManager: PasswordManager? = null
    var serviceHelper: ServiceHelper? = null
    var currentActivity: AppCompatActivity? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        serviceHelper = ServiceHelper(this)
        identityManager = PasswordManager()

        val myWorkBuilder = PeriodicWorkRequest.Builder(PermissionsWorker::class.java, 24, TimeUnit.HOURS)

        val myWork = myWorkBuilder.build()
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("PermissionsWorker", ExistingPeriodicWorkPolicy.KEEP, myWork)
    }

    companion object {
        @Volatile
        var instance: PriVELTApplication? = null
            private set
    }
}