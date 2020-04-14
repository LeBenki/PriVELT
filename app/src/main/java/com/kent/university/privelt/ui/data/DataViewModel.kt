/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.repositories.ServiceDataRepository
import com.kent.university.privelt.repositories.UserDataRepository

class DataViewModel(private val mUserDataRepository: UserDataRepository, private val mServiceDataRepository: ServiceDataRepository) : ViewModel() {
    var services: LiveData<List<Service>>? = null
        private set

    fun init() {
        if (services == null) services = mServiceDataRepository.services
    }

    fun getUserData(id: Long, type: String?): LiveData<List<UserData>> {
        return mUserDataRepository.getUserDatasForAServiceAndType(id, type)
    }

}