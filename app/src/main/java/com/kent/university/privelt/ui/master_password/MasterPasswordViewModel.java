package com.kent.university.privelt.ui.master_password;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.ViewModel;

public class MasterPasswordViewModel extends ViewModel {
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final Executor mExecutor;

    public MasterPasswordViewModel(UserDataRepository mUserDataSource, ServiceDataRepository mServiceDataSource, Executor mExecutor) {
        this.mUserDataSource = mUserDataSource;
        this.mServiceDataSource = mServiceDataSource;
        this.mExecutor = mExecutor;
    }


    void deleteAllDatabase() {
        mUserDataSource.deleteAllUserDatas();
        mServiceDataSource.deleteAllServices();
        //TODO: delete also current_user
    }

    List<Service> getAllServices() {
        return mServiceDataSource.getAllServices();
    }
}
