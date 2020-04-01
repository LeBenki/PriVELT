/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.card;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class CardViewModel extends ViewModel {
    private final ServiceDataRepository mServiceDataSource;
    private final UserDataRepository mUserDataSource;
    private final Executor mExecutor;

    @Nullable
    private LiveData<List<Service>> mServices;

    private LiveData<List<UserData>> mUserDatas;

    public CardViewModel(ServiceDataRepository serviceDataRepository, UserDataRepository userDataSource, Executor executor) {
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
        mServiceDataSource.insertServices(services);
    }

    void deleteService(Service... services) {
        mExecutor.execute(() -> mServiceDataSource.deleteServices(services));
    }

    void updateService(Service... services) {
         mServiceDataSource.updateServices(services);
    }

    LiveData<List<UserData>> getUserDatas() {
        return mUserDatas;
    }
}