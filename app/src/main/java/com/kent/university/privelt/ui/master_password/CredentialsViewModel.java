package com.kent.university.privelt.ui.master_password;

import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CredentialsViewModel extends ViewModel {
    private final CredentialsDataRepository mCredentialsDataSource;
    private final UserDataRepository mUserDataSource;
    private final Executor mExecutor;
    private MutableLiveData<Boolean> changePassword;

    public CredentialsViewModel(CredentialsDataRepository mCredentialsDataSource, UserDataRepository mUserDataSource, Executor mExecutor) {
        this.mCredentialsDataSource = mCredentialsDataSource;
        this.mUserDataSource = mUserDataSource;
        this.mExecutor = mExecutor;
        this.changePassword = new MutableLiveData<>();
        changePassword.setValue(false);
    }

    public LiveData<Boolean> getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword.setValue(changePassword);
    }

    @Nullable
    public LiveData<List<Credentials>> getCredentials() {
        return mCredentialsDataSource.getCredentials();
    }

    public Credentials getCredentialsWithId(long id) {
        return mCredentialsDataSource.getCredentialsWithId(id);
    }

    public void updateCredentials(Credentials... credentials) {
        mCredentialsDataSource.updateCredentials(credentials);
    }

    public LiveData<List<UserData>> getUserDatas() {
        return mUserDataSource.getUserDatas();
    }
}
