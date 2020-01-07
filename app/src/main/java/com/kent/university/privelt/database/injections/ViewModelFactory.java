package com.kent.university.privelt.database.injections;

import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.ui.dashboard.DashboardViewModel;
import com.kent.university.privelt.ui.master_password.CredentialsViewModel;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final CredentialsDataRepository mProjectDataSource;
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final Executor mExecutor;

    ViewModelFactory(CredentialsDataRepository credentialsDataSource, UserDataRepository userDataSource, ServiceDataRepository serviceSource, Executor executor) {
        mProjectDataSource = credentialsDataSource;
        mUserDataSource = userDataSource;
        mServiceDataSource = serviceSource;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (CredentialsViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(CredentialsDataRepository.class,
                        Executor.class)
                        .newInstance(mProjectDataSource, mExecutor);
            }
            else if (DashboardViewModel.class.equals(modelClass)) {
                return modelClass.getConstructor(ServiceDataRepository.class,
                        Executor.class)
                        .newInstance(mServiceDataSource, mExecutor);
            }
            else
                throw new IllegalArgumentException("Unknown ViewModel class");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}