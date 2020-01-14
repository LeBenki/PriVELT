package com.kent.university.privelt.ui.master_password;

import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MasterPasswordViewModel extends ViewModel {
    private final CredentialsDataRepository mCredentialsDataSource;
    private final UserDataRepository mUserDataSource;
    private final ServiceDataRepository mServiceDataSource;
    private final Executor mExecutor;

    public MasterPasswordViewModel(CredentialsDataRepository mCredentialsDataSource, UserDataRepository mUserDataSource, ServiceDataRepository mServiceDataSource, Executor mExecutor) {
        this.mCredentialsDataSource = mCredentialsDataSource;
        this.mUserDataSource = mUserDataSource;
        this.mServiceDataSource = mServiceDataSource;
        this.mExecutor = mExecutor;
    }

    @Nullable
    public LiveData<List<Credentials>> getCredentials() {
        return mCredentialsDataSource.getCredentials();
    }

    Credentials getCredentialsWithId(long id) {
        return mCredentialsDataSource.getCredentialsWithId(id);
    }

    void updateCredentials(Credentials... credentials) {
        mCredentialsDataSource.updateCredentials(credentials);
    }

    void deleteAllDatabase() {
        mCredentialsDataSource.deleteAllCredentials();
        mUserDataSource.deleteAllUserDatas();
        mServiceDataSource.deleteAllServices();
    }

    List<Service> getAllServices() {
        return mServiceDataSource.getAllServices();
    }
}
