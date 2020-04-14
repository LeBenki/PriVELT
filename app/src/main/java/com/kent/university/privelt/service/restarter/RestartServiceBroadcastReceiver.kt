/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.service.restarter

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.kent.university.privelt.service.ProcessMainClass
import com.kent.university.privelt.service.Service

class RestartServiceBroadcastReceiver : BroadcastReceiver() {
    private var restartSensorServiceReceiver: RestartServiceBroadcastReceiver? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(context)
        } else {
            registerRestarterReceiver(context)
            val bck = ProcessMainClass()
            bck.launchService(context)
        }
    }

    private fun registerRestarterReceiver(context: Context) {
        if (restartSensorServiceReceiver == null) restartSensorServiceReceiver = RestartServiceBroadcastReceiver() else try {
            context.unregisterReceiver(restartSensorServiceReceiver)
        } catch (e: Exception) {
            // not registered
        }
        // give the time to run
        Handler().postDelayed({
            val filter = IntentFilter()
            filter.addAction(Service.RESTART_INTENT)
            try {
                context.registerReceiver(restartSensorServiceReceiver, filter)
            } catch (e: Exception) {
                try {
                    context.applicationContext.registerReceiver(restartSensorServiceReceiver, filter)
                } catch (ex: Exception) {
                }
            }
        }, 1000)
    }

    companion object {
        private var jobScheduler: JobScheduler? = null

        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun scheduleJob(context: Context) {
            if (jobScheduler == null) {
                jobScheduler = context
                        .getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            }
            val componentName = ComponentName(context,
                    JobService::class.java)
            val jobInfo = JobInfo.Builder(1, componentName) // setOverrideDeadline runs it immediately - you must have at least one constraint
                    // https://stackoverflow.com/questions/51064731/firing-jobservice-without-constraints
                    .setOverrideDeadline(0)
                    .setPersisted(true).build()
            jobScheduler!!.schedule(jobInfo)
        }
    }
}