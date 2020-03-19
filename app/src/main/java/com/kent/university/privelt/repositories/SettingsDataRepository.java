package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.SettingsDao;
import com.kent.university.privelt.model.Settings;

import androidx.lifecycle.LiveData;

public class SettingsDataRepository {
    private final SettingsDao mSettingsDao;

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
