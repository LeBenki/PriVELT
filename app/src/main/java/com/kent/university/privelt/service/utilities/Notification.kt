/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.service.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kent.university.privelt.R
import com.kent.university.privelt.ui.dashboard.DashboardActivity

class Notification {
    private var notificationPendingIntent: PendingIntent? = null

    /**
     * This is the method  called to create the Notification
     */
    fun setNotification(context: Context, title: String?, text: String?, icon: Int): Notification {
        if (notificationPendingIntent == null) {
            val notificationIntent = Intent(context, DashboardActivity::class.java)
            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        }
        val notification: Notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name: CharSequence = "Permanent Notification"
            //mContext.getString(R.string.channel_name);
            val importance = NotificationManager.IMPORTANCE_LOW
            val CHANNEL_ID = "kent.university.channel"
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            //String description = mContext.getString(R.string.notifications_description);
            val description = "I would like to receive travel alerts and notifications for:"
            channel.description = description
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
            notification = notificationBuilder //the log is PNG file format with a transparent background
                    .setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(notificationPendingIntent)
                    .build()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification = NotificationCompat.Builder(context, "channel") // to be defined in the MainActivity of the app
                    .setSmallIcon(icon)
                    .setContentTitle(title) //                    .setColor(mContext.getResources().getColor(R.color.colorAccent))
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build()
        } else {
            notification = NotificationCompat.Builder(context, "channel") // to be defined in the MainActivity of the app
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build()
        }
        return notification
    }
}