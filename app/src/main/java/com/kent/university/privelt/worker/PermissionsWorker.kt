/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kent.university.privelt.utils.sensors.TemporarySavePermissions
import com.kent.university.privelt.utils.sensors.TemporarySaveSensors

class PermissionsWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams)  {

    override fun doWork(): Result {
        val time = System.currentTimeMillis()
        TemporarySaveSensors.save(applicationContext, time)
        TemporarySavePermissions.save(applicationContext, time)
        return Result.success()
    }

}