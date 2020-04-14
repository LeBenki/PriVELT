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
import com.kent.university.privelt.model.Settings

@Dao
interface SettingsDao {
    @get:Query("SELECT * FROM settings WHERE id = 0")
    val settings: LiveData<Settings>?

    @get:Query("SELECT * FROM settings WHERE id = 0")
    val instantSettings: Settings

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateSettings(vararg user: Settings?)

    @Query("DELETE FROM settings")
    fun deleteSettings()
}