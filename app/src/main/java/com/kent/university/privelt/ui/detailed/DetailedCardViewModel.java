/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.detailed;

import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DetailedCardViewModel extends ViewModel {
    private final UserDataRepository mUserDataRepository;
    private final Executor mExecutor;

    private LiveData<List<UserData>> userDatas;

    public DetailedCardViewModel(UserDataRepository mUserDataRepository, Executor mExecutor) {
        this.mUserDataRepository = mUserDataRepository;
        this.mExecutor = mExecutor;
    }

    void init(long serviceId) {
    }

    LiveData<List<UserData>> getUserDatasForService(long serviceId) {
        return userDatas;
    }
}
