/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kent.university.privelt.model.Service

@Dao
interface ServiceDao {
    @get:Query("SELECT * FROM service")
    val services: LiveData<List<Service?>?>?

    @get:Query("SELECT * FROM service")
    val allServices: List<Service?>?

    @Insert
    fun insertServices(vararg service: Service?)

    @Delete
    fun deleteServices(vararg service: Service?)

    @Update
    fun updateServices(vararg services: Service?)

    @Query("DELETE FROM service")
    fun deleteAllServices()
}