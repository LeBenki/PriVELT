/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.risk_value

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.repositories.ServiceDataRepository
import com.kent.university.privelt.repositories.UserDataRepository

class RiskValueViewModel(private val mServiceDataSource: ServiceDataRepository, private val mUserDataSource: UserDataRepository) : ViewModel() {
    var services: LiveData<List<Service>>? = null
        private set
    var userDatas: LiveData<List<UserData>>? = null
        private set

    fun init() {
        if (services == null) services = mServiceDataSource.services
        if (userDatas == null) userDatas = mUserDataSource.userDatas
    }

}