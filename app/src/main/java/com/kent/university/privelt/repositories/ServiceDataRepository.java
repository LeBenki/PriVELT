package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.model.Service;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ServiceDataRepository {
    private final ServiceDao mServiceDao;

    public ServiceDataRepository(ServiceDao projectDao) {
        mServiceDao = projectDao;
    }

    public LiveData<List<Service>> getServices() {
        return mServiceDao.getServices();
    }

    public void deleteServices(Service... credentials) {
        mServiceDao.deleteServices(credentials);
    }

    public void insertServices(Service... credentials) {
        mServiceDao.insertServices(credentials);
    }

    public void updateServices(Service... credentials) {
        mServiceDao.updateServices(credentials);
    }
}
