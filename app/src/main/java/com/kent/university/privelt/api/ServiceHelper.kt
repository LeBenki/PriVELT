/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.api

import android.content.Context
import com.kent.university.privelt.model.Service
import net.neferett.webviewsinjector.services.LoginService
import net.neferett.webviewsinjector.services.ServiceManager
import java.util.*

class ServiceHelper(context: Context?) {
    private val serviceManager: ServiceManager = ServiceManager(context)
    private val serviceNames: ArrayList<String>
    private val serviceResIds: ArrayList<Int>
    fun getRemainingServices(subscribedServices: List<Service>): List<String> {
        val notSubscribed = ArrayList<String>()
        notSubscribed.addAll(serviceNames)
        //Avoid not working services
        notSubscribed.remove("Strava")
        notSubscribed.remove("Trainline")
        for ((name) in subscribedServices) notSubscribed.remove(name)
        return notSubscribed
    }

    fun getResIdWithName(name: String): Int {
        for (i in 0 until serviceNames.size) {
            if (serviceNames[i] == name) return serviceResIds[i]
        }
        return -1
    }

    fun getServiceWithName(name: String): LoginService? {
        for (i in 0 until serviceNames.size) {
            if (serviceNames[i] == name) {
                return serviceManager.serviceList[i]
            }
        }
        return null
    }

    init {
        serviceManager.loadServices()
        serviceNames = ArrayList()
        serviceResIds = ArrayList()
        for (loginService in serviceManager.serviceList) {
            serviceNames.add(loginService.name)
            serviceResIds.add(loginService.drawableLogo)
        }
    }
}