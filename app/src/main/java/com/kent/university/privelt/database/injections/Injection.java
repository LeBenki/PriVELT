package com.kent.university.privelt.database.injections;

import android.content.Context;

import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.repositories.CurrentUserDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.SettingsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static UserDataRepository provideUserDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new UserDataRepository(database.userDataDao());
    }

    public static ServiceDataRepository provideServiceDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new ServiceDataRepository(database.serviceDao());
    }

    private static CurrentUserDataRepository provideCurrentUserDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new CurrentUserDataRepository(database.currentUserDao());
    }

    public static SettingsDataRepository provideSettingsDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new SettingsDataRepository(database.settingsDao());
    }

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        UserDataRepository userDataSource = provideUserDataSource(context);
        ServiceDataRepository serviceSource = provideServiceDataSource(context);
        CurrentUserDataRepository currentUserSource = provideCurrentUserDataSource(context);
        SettingsDataRepository settingsSource = provideSettingsDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(userDataSource, serviceSource, currentUserSource, settingsSource, executor);
    }
}
