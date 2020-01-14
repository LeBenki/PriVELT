package com.kent.university.privelt.database.injections;

import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.ui.dashboard.DashboardViewModel;
import com.kent.university.privelt.ui.data.DataViewModel;
import com.kent.university.privelt.ui.master_password.MasterPasswordViewModel;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final CredentialsDataRepository mCredentialsDataSource;
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final Executor mExecutor;

    ViewModelFactory(CredentialsDataRepository credentialsDataSource, UserDataRepository userDataSource, ServiceDataRepository serviceSource, Executor executor) {
        mCredentialsDataSource = credentialsDataSource;
        mUserDataSource = userDataSource;
        mServiceDataSource = serviceSource;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (MasterPasswordViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(CredentialsDataRepository.class,
                        UserDataRepository.class,
                        ServiceDataRepository.class,
                        Executor.class)
                        .newInstance(mCredentialsDataSource, mUserDataSource, mServiceDataSource, mExecutor);
            }
            else if (DashboardViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(ServiceDataRepository.class,
                        CredentialsDataRepository.class,
                        Executor.class)
                        .newInstance(mServiceDataSource, mCredentialsDataSource, mExecutor);
            }
            else if (DataViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(UserDataRepository.class,
                        Executor.class)
                        .newInstance(mUserDataSource, mExecutor);
            }
            else
                throw new IllegalArgumentException("Unknown ViewModel class");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}