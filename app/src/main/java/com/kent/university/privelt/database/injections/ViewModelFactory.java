package com.kent.university.privelt.database.injections;

import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.ui.CredentialsViewModel;

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
        if (modelClass.isAssignableFrom(CredentialsViewModel.class)) {
            return (T) new CredentialsViewModel(mProjectDataSource, mTaskDataSource, mExecutor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}