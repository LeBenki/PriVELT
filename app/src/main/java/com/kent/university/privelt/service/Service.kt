/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.api.DataExtraction.processExtractionForEachService
import com.kent.university.privelt.service.utilities.Notification
import java.util.*

open class Service : Service() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            val bck = ProcessMainClass()
            bck.launchService(this)
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground()
        }
        process()

        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    fun restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val notification = Notification()
                startForeground(NOTIFICATION_ID, notification.setNotification(this, "Service notification", "We are extracting data for you", R.drawable.ic_sleep))
                process()
            } catch (ignored: Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // restart the never ending service
        val broadcastIntent = Intent(RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        // restart the never ending service
        val broadcastIntent = Intent(RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }

    fun process() {

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask()
        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                if ((applicationContext as PriVELTApplication).identityManager!!.password != null) processExtractionForEachService(applicationContext)
            }
        }

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000 * 60 * 60.toLong())
    }

    /**
     * not needed
     */
    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    companion object {
        const val RESTART_INTENT = "uk.ac.shef.oak.restarter"
        protected const val NOTIFICATION_ID = 1337

        /**
         * static to avoid multiple timers to be created when the service is called several times
         */
        private var timer: Timer? = null
    }
}