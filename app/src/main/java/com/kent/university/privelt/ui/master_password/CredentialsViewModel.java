package com.kent.university.privelt.ui.master_password;

import com.kent.university.privelt.model.Credentials;
import com.kent.university.privelt.repositories.CredentialsDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class CredentialsViewModel extends ViewModel {
    private final CredentialsDataRepository mCredentialsDataSource;
    private final Executor mExecutor;

    public CredentialsViewModel(CredentialsDataRepository mCredentialsDataSource, Executor mExecutor) {
        this.mCredentialsDataSource = mCredentialsDataSource;
        this.mExecutor = mExecutor;
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
}
