/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.ServiceDao;
import com.kent.university.privelt.model.Service;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

public class ServiceDataRepository {
    private final ServiceDao mServiceDao;

    @Inject
    public ServiceDataRepository(ServiceDao projectDao) {
        mServiceDao = projectDao;
    }

    public LiveData<List<Service>> getServices() {
        return mServiceDao.getServices();
    }

    public List<Service> getAllServices() {
        return mServiceDao.getAllServices();
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

    public void deleteAllServices() {
        mServiceDao.deleteAllServices();
    }
}
