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
import com.kent.university.privelt.model.UserData

@Dao
interface UserDataDao {
    @get:Query("SELECT * FROM user_data")
    val userData: LiveData<List<UserData>>?

    @Query("DELETE FROM user_data")
    fun deleteAllUserData()

    @Query("DELETE FROM user_data WHERE service_id = :serviceId")
    fun deleteUserDataForAService(serviceId: Long)

    @Query("SELECT * FROM user_data WHERE service_id = :serviceId and type =:type")
    fun getUserDataForAServiceAndType(serviceId: Long, type: String?): LiveData<List<UserData>>?

    @Query("SELECT * FROM user_data WHERE service_id = :serviceId")
    fun getUserDataForAService(serviceId: Long): List<UserData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(vararg userData: UserData?)
}