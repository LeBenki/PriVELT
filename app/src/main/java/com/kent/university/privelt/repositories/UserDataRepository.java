package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.UserDataDao;
import com.kent.university.privelt.model.UserData;

import java.util.List;

import androidx.lifecycle.LiveData;

public class UserDataRepository {
    private final UserDataDao mUserDataDao;

    public UserDataRepository(UserDataDao projectDao) {
        mUserDataDao = projectDao;
    }

    public LiveData<List<UserData>> getUserDatas() {
        return mUserDataDao.getUserDatas();
    }
}