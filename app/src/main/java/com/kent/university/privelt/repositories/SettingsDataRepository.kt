/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.repositories

import androidx.lifecycle.LiveData
import com.kent.university.privelt.database.dao.SettingsDao
import com.kent.university.privelt.model.Settings
import javax.inject.Inject

class SettingsDataRepository @Inject constructor(private val mSettingsDao: SettingsDao) {
    val settings: LiveData<Settings>
        get() = mSettingsDao.settings!!

    fun updateSettings(vararg settings: Settings?) {
        mSettingsDao.updateSettings(*settings)
    }
}