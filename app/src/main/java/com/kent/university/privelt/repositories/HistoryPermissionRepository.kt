/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.HistoryPermissionDao
import com.kent.university.privelt.model.HistoryPermission
import javax.inject.Inject

class HistoryPermissionRepository @Inject constructor(private val mHistoryPermissionDao: HistoryPermissionDao) {

    fun getPermissions(): LiveData<List<HistoryPermission>> {
        return mHistoryPermissionDao.getHistoryPermissions()
    }
}