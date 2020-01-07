package com.kent.university.privelt.ui.dashboard;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.repositories.ServiceDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    private final ServiceDataRepository mServiceDataSource;
    private final Executor mExecutor;

    @Nullable
    private LiveData<List<Service>> mServices;

    public DashboardViewModel(ServiceDataRepository serviceDataRepository, Executor executor) {
        mServiceDataSource = serviceDataRepository;
        mExecutor = executor;
    }

    public void init() {
        if (mServices == null)
            mServices = mServiceDataSource.getServices();
    }

    @Nullable
    public LiveData<List<Service>> getServices() {
        return mServices;
    }

    public void updateService(Service... services) {
        mExecutor.execute(() -> mServiceDataSource.updateServices(services));
    }
}