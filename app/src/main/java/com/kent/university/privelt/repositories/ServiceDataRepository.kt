/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.ServiceDao
import com.kent.university.privelt.model.Service
import javax.inject.Inject

class ServiceDataRepository @Inject constructor(private val mServiceDao: ServiceDao) {
    val services: LiveData<List<Service>>
        get() = mServiceDao.services!!

    val allServices: List<Service>
        get() = mServiceDao.allServices!!

    fun deleteServices(vararg credentials: Service?) {
        mServiceDao.deleteServices(*credentials)
    }

    fun insertServices(vararg credentials: Service?) {
        mServiceDao.insertServices(*credentials)
    }

    fun updateServices(vararg credentials: Service?) {
        mServiceDao.updateServices(*credentials)
    }

    fun deleteAllServices() {
        mServiceDao.deleteAllServices()
    }

}