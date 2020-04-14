/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.CurrentUserDao
import com.kent.university.privelt.model.CurrentUser
import javax.inject.Inject

class CurrentUserDataRepository @Inject constructor(private val mCurrentUserDao: CurrentUserDao) {
    val currentUser: LiveData<CurrentUser>
        get() = mCurrentUserDao.currentUser!!

    fun updateCurrentUser(vararg currentUsers: CurrentUser?) {
        mCurrentUserDao.updateCurrentUser(*currentUsers)
    }

    fun deleteCurrentUser() {
        mCurrentUserDao.deleteCurrentUser()
    }

}