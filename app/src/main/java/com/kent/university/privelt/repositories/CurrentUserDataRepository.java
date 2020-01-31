package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.CurrentUserDao;
import com.kent.university.privelt.model.CurrentUser;

import androidx.lifecycle.LiveData;

public class CurrentUserDataRepository {
    private final CurrentUserDao mCurrentUserDao;

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
