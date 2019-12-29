package com.kent.university.privelt.database.injections;

import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final CredentialsDataRepository mProjectDataSource;
    private final UserDataRepository mTaskDataSource;
    private final Executor mExecutor;

    ViewModelFactory(CredentialsDataRepository credentialsDataSource, UserDataRepository userDataSource, Executor executor) {
        mProjectDataSource = credentialsDataSource;
        mTaskDataSource = userDataSource;
        mExecutor = executor;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(CredentialsDataRepository.class,
                    UserDataRepository.class,
                    Executor.class)
                    .newInstance(mProjectDataSource, mTaskDataSource, mExecutor);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}