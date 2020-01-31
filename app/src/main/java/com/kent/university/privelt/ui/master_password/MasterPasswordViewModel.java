package com.kent.university.privelt.ui.master_password;

import com.kent.university.privelt.repositories.CurrentUserDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.concurrent.Executor;

import androidx.lifecycle.ViewModel;

public class MasterPasswordViewModel extends ViewModel {
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final CurrentUserDataRepository mCurrentUserDataRepository;
    private final Executor mExecutor;

    public MasterPasswordViewModel(UserDataRepository mUserDataSource, ServiceDataRepository mServiceDataSource, CurrentUserDataRepository mCurrentUserDataRepository, Executor mExecutor) {
        this.mUserDataSource = mUserDataSource;
        this.mServiceDataSource = mServiceDataSource;
        this.mCurrentUserDataRepository = mCurrentUserDataRepository;
        this.mExecutor = mExecutor;
    }


    void deleteAllDatabase() {
        mUserDataSource.deleteAllUserDatas();
        mServiceDataSource.deleteAllServices();
        mCurrentUserDataRepository.deleteCurrentUser();
    }
}
