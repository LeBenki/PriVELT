/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.Settings
import com.kent.university.privelt.repositories.SettingsDataRepository
import java.util.concurrent.Executor

class SettingsViewModel(private val mSettingsRepository: SettingsDataRepository, private val mExecutor: Executor) : ViewModel() {
    var settings: LiveData<Settings>? = null
        private set

    fun init() {
        if (settings == null) settings = mSettingsRepository.settings
    }

    fun updateSettings(vararg settings: Settings?) {
        mExecutor.execute { mSettingsRepository.updateSettings(*settings) }
    }

}