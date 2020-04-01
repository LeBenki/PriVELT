/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.SettingsDao;
import com.kent.university.privelt.model.Settings;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

public class SettingsDataRepository {
    private final SettingsDao mSettingsDao;

    @Inject
    public SettingsDataRepository(SettingsDao settingsDao) {
        mSettingsDao = settingsDao;
    }

    public LiveData<Settings> getSettings() {
        return mSettingsDao.getSettings();
    }

    public Settings getInstantSettings() {
        return mSettingsDao.getInstantSettings();
    }

    public void updateSettings(Settings... settings) {
        mSettingsDao.updateSettings(settings);
    }

    public void deleteSettings() {
        mSettingsDao.deleteSettings();
    }
}
