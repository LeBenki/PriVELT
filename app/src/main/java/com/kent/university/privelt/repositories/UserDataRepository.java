/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.UserData;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

public class UserDataRepository {
    private final UserDataDao mUserDataDao;

    @Inject
    public UserDataRepository(UserDataDao projectDao) {
        mUserDataDao = projectDao;
    }

    public LiveData<List<UserData>> getUserDatas() {
        return mUserDataDao.getUserDatas();
    }

    public void deleteAllUserDatas() {
        mUserDataDao.deleteAllUserDatas();
    }

    public void insertUserDatas(UserData... userData) {
        mUserDataDao.insertUserDatas(userData);
    }

    public void deleteUserDatasForAService(long id) {
        mUserDataDao.deleteUserDatasForAService(id);
    }

    public LiveData<List<UserData>> getUserDatasForAServiceAndType(long service, String type) {
        return mUserDataDao.getUserDatasForAServiceAndType(service, type);
    }
}
