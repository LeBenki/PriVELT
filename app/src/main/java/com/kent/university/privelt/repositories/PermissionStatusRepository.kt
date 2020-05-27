/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.PermissionStatusDao
import com.kent.university.privelt.model.PermissionStatus
import javax.inject.Inject

class PermissionStatusRepository  @Inject constructor(private val mPermissionStatusDao: PermissionStatusDao) {

    fun getPermissions(dateL: Long, dateR: Long): LiveData<List<PermissionStatus>> {
        return mPermissionStatusDao.getPermissions(dateL, dateR)
    }
}