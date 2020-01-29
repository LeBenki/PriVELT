package com.kent.university.privelt.ui.dashboard.user;

import com.kent.university.privelt.model.CurrentUser;
import com.kent.university.privelt.repositories.CurrentUserDataRepository;

import java.util.concurrent.Executor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final CurrentUserDataRepository mCurrentUserRepository;
    private final Executor mExecutor;

    private LiveData<CurrentUser> currentUser;

    public UserViewModel(CurrentUserDataRepository mCurrentUserRepository, Executor mExecutor) {
        this.mCurrentUserRepository = mCurrentUserRepository;
        this.mExecutor = mExecutor;
    }

    void init() {
        if (currentUser == null)
            currentUser = mCurrentUserRepository.getCurrentUser();
    }

    LiveData<CurrentUser> getCurrentUser() {
        return currentUser;
    }

    void updateCurrentUser(CurrentUser... currentUser) {
        mExecutor.execute(() -> mCurrentUserRepository.updateCurrentUser(currentUser));
    }
}
