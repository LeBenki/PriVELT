/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.CurrentUserDao;
import com.kent.university.privelt.model.CurrentUser;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;

public class CurrentUserDataRepository {
    private final CurrentUserDao mCurrentUserDao;

    @Inject
    public CurrentUserDataRepository(CurrentUserDao currentUserDao) {
        mCurrentUserDao = currentUserDao;
    }

    public LiveData<CurrentUser> getCurrentUser() {
        return mCurrentUserDao.getCurrentUser();
    }


    public void updateCurrentUser(CurrentUser... currentUsers) {
        mCurrentUserDao.updateCurrentUser(currentUsers);
    }

    public void deleteCurrentUser() {
        mCurrentUserDao.deleteCurrentUser();
    }
}
