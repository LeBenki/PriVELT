package com.kent.university.privelt.ui.settings;

import com.kent.university.privelt.model.Settings;
import com.kent.university.privelt.repositories.SettingsDataRepository;

import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final SettingsDataRepository mSettingsRepository;
    private final Executor mExecutor;

    private LiveData<Settings> settings;

    public SettingsViewModel(SettingsDataRepository mSettingsRepository, Executor mExecutor) {
        this.mSettingsRepository = mSettingsRepository;
        this.mExecutor = mExecutor;
    }

    public void init() {
        if (settings == null)
            settings = mSettingsRepository.getSettings();
    }

    public LiveData<Settings> getSettings() {
        return settings;
    }

    void updateSettings(Settings... settings) {
        mExecutor.execute(() -> mSettingsRepository.updateSettings(settings));
    }
}
