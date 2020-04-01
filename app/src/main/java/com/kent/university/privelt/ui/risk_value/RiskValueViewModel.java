/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.risk_value;

import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class RiskValueViewModel extends ViewModel {
    private final ServiceDataRepository mServiceDataSource;
    private final UserDataRepository mUserDataSource;

    @Nullable
    private LiveData<List<Service>> mServices;

    @Nullable
    private LiveData<List<UserData>> mUserDatas;

    public RiskValueViewModel(ServiceDataRepository serviceDataRepository, UserDataRepository userDataSource) {
        mServiceDataSource = serviceDataRepository;
        mUserDataSource = userDataSource;
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

    LiveData<List<UserData>> getUserDatas() {
        return mUserDatas;
    }
}