package com.kent.university.privelt.ui.dashboard.service;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ServiceViewModel extends ViewModel {
    private final ServiceDataRepository mServiceDataSource;
    private final UserDataRepository mUserDataSource;
    private final Executor mExecutor;

    @Nullable
    private LiveData<List<Service>> mServices;

    private LiveData<List<UserData>> mUserDatas;

    public ServiceViewModel(ServiceDataRepository serviceDataRepository, UserDataRepository userDataSource, Executor executor) {
        mServiceDataSource = serviceDataRepository;
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

    LiveData<List<UserData>> getUserDatas() {
        return mUserDatas;
    }
}