package com.kent.university.privelt.repositories;

import com.kent.university.privelt.database.dao.CredentialsDao;
import com.kent.university.privelt.model.Credentials;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CredentialsDataRepository {
    private final CredentialsDao mCredentialsDao;

    public CredentialsDataRepository(CredentialsDao projectDao) {
        mCredentialsDao = projectDao;
    }

    public LiveData<List<Credentials>> getCredentials() {
        return mCredentialsDao.getCredentials();
    }

    public Credentials getCredentialsWithId(long id){
        return mCredentialsDao.getCredentialsWithId(id);
    }

    public void updateCredentials(Credentials... credentials) {
        mCredentialsDao.updateCredentials(credentials);
    }
}
