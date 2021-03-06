/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kent.university.privelt.model.HistoryPermission

@Dao
interface HistoryPermissionDao {
    @Query("SELECT * FROM history_permission ORDER BY date")
    fun getHistoryPermissions(): LiveData<List<HistoryPermission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryPermissionStatus(vararg permissionStatus: HistoryPermission?)
}