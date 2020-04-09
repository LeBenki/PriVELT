/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.card.detailed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.repositories.UserDataRepository
import java.util.concurrent.Executor

class DetailedCardViewModel(private val mUserDataRepository: UserDataRepository, private val mExecutor: Executor) : ViewModel() {
    private val userDatas: LiveData<List<UserData>>? = null
    fun init(serviceId: Long) {}
    fun getUserDatasForService(serviceId: Long): LiveData<List<UserData>>? {
        return userDatas
    }

}