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
