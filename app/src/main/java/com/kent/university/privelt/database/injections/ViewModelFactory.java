package com.kent.university.privelt.database.injections;

import com.kent.university.privelt.repositories.CurrentUserDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.SettingsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.ui.detailed.DetailedCardViewModel;
import com.kent.university.privelt.ui.risk_value.RiskValueViewModel;
import com.kent.university.privelt.ui.dashboard.card.CardViewModel;
import com.kent.university.privelt.ui.dashboard.user.UserViewModel;
import com.kent.university.privelt.ui.data.DataViewModel;
import com.kent.university.privelt.ui.settings.SettingsViewModel;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final CurrentUserDataRepository mCurrentUserDataRepository;
    private final SettingsDataRepository mSettingsDataSource;
    private final Executor mExecutor;

    ViewModelFactory(UserDataRepository userDataSource, ServiceDataRepository serviceSource, CurrentUserDataRepository currentUserDataRepository, SettingsDataRepository settingsSource, Executor executor) {
        mUserDataSource = userDataSource;
        mServiceDataSource = serviceSource;
        mCurrentUserDataRepository = currentUserDataRepository;
        mSettingsDataSource = settingsSource;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (CardViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(ServiceDataRepository.class,
                        UserDataRepository.class,
                        Executor.class)
                        .newInstance(mServiceDataSource, mUserDataSource, mExecutor);
            }
            else if (DataViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(UserDataRepository.class,
                        ServiceDataRepository.class,
                        Executor.class)
                        .newInstance(mUserDataSource, mServiceDataSource, mExecutor);
            }
            else if (RiskValueViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(ServiceDataRepository.class,
                        UserDataRepository.class)
                        .newInstance(mServiceDataSource, mUserDataSource);
            }
            else if (UserViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(CurrentUserDataRepository.class,
                        Executor.class)
                        .newInstance(mCurrentUserDataRepository, mExecutor);
            }
            else if (DetailedCardViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(UserDataRepository.class,
                        Executor.class)
                        .newInstance(mUserDataSource, mExecutor);
            }
            else if (SettingsViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(SettingsDataRepository.class,
                        Executor.class)
                        .newInstance(mSettingsDataSource, mExecutor);
            }
            else
                throw new IllegalArgumentException("Unknown ViewModel class");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}