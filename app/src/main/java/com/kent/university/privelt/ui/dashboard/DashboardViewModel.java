package com.kent.university.privelt.ui.dashboard;

import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    private final ServiceDataRepository mServiceDataSource;
    private final CredentialsDataRepository mCredentialsDataSource;
    private final UserDataRepository mUserDataSource;
    private final Executor mExecutor;

    @Nullable
    private LiveData<List<Service>> mServices;

    private LiveData<List<UserData>> mUserDatas;

    public DashboardViewModel(ServiceDataRepository serviceDataRepository, CredentialsDataRepository credentialsDataRepository, UserDataRepository userDataSource, Executor executor) {
        mServiceDataSource = serviceDataRepository;
        mCredentialsDataSource = credentialsDataRepository;
        mUserDataSource = userDataSource;
        mExecutor = executor;
    }

    void init() {
        if (mServices == null)
            mServices = mServiceDataSource.getServices();
        if (mUserDatas == null)
            mUserDatas = mUserDataSource.getUserDatas();
    }

    @Nullable
    public LiveData<List<Service>> getServices() {
        return mServices;
    }

    void insertService(Service... services) {
        mExecutor.execute(() -> mServiceDataSource.insertServices(services));
    }

    void deleteService(Service... services) {
        mExecutor.execute(() -> mServiceDataSource.deleteServices(services));
    }

    void updateService(Service... services) {
        mExecutor.execute(() -> mServiceDataSource.updateServices(services));
    }

    void updateCredentials(Credentials credentials) {
        mExecutor.execute(() -> mCredentialsDataSource.updateCredentials(credentials));
    }

    Credentials getCredentialsWithId(long id) {
        return mCredentialsDataSource.getCredentialsWithId(id);
    }

    LiveData<List<UserData>> getUserDatas() {
        return mUserDatas;
    }
}