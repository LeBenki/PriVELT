package com.kent.university.privelt.ui.data;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {
    private final UserDataRepository mUserDataRepository;
    private final ServiceDataRepository mServiceDataRepository;
    private final Executor mExecutor;

    private LiveData<List<Service>> services;


    public DataViewModel(UserDataRepository mUserDataRepository, ServiceDataRepository mServiceDataRepository, Executor mExecutor) {
        this.mUserDataRepository = mUserDataRepository;
        this.mServiceDataRepository = mServiceDataRepository;
        this.mExecutor = mExecutor;
    }

    void init() {
        if (services == null)
            services = mServiceDataRepository.getServices();
    }

    LiveData<List<Service>> getServices() {
        return services;
    }

    LiveData<List<UserData>> getUserData(long id, String type) {
        return mUserDataRepository.getUserDatasForAServiceAndType(id, type);
    }
}
