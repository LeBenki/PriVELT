package com.kent.university.privelt.ui.data;

import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {
    private final UserDataRepository mUserDataRepository;
    private final Executor mExecutor;

    private LiveData<List<UserData>> userDatas;

    public DataViewModel(UserDataRepository mUserDataRepository, Executor mExecutor) {
        this.mUserDataRepository = mUserDataRepository;
        this.mExecutor = mExecutor;
    }

    public void init() {
        if (userDatas == null)
            userDatas = mUserDataRepository.getUserDatas();
    }

    public LiveData<List<UserData>> getUserDatas() {
        return userDatas;
    }

    public void replaceUserDatas(UserData... userDatas) {
        mExecutor.execute(() -> {
            mUserDataRepository.deleteAllUserDatas();
            mUserDataRepository.insertUserDatas(userDatas);
        });
    }
}
