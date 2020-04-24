/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.sensors.detailed

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kent.university.privelt.model.Application
import kotlinx.android.synthetic.main.cell_detailed_sensor.view.*


internal class DetailedSensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private fun getAppLabel(context: Context, packageName: String) : String {
        val packageManager = context.packageManager;
        var applicationInfo : ApplicationInfo? = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return ((if (applicationInfo != null)  packageManager.getApplicationLabel(applicationInfo) else "Unknown").toString())
    }

    fun bind(application: Application) {
        itemView.title!!.text = getAppLabel(itemView.context, application.packageName)

        try {
            val icon: Drawable = itemView.context.packageManager.getApplicationIcon(application.packageName)
            itemView.logo!!.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

}