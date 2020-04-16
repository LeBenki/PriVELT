/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.UserDataDao
import com.kent.university.privelt.model.UserData
import javax.inject.Inject

class UserDataRepository @Inject constructor(private val mUserDataDao: UserDataDao) {
    val userDatas: LiveData<List<UserData>>
        get() = mUserDataDao.userData!!

    fun getUserDatasForAServiceAndType(service: Long, type: String?): LiveData<List<UserData>> {
        return mUserDataDao.getUserDataForAServiceAndType(service, type)!!
    }

}