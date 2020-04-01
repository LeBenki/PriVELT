package com.kent.university.privelt.di;

import android.content.Context;

import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.repositories.CurrentUserDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.SettingsDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;
import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModelProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private Context context;

    public RoomModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    UserDataRepository provideUserDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new UserDataRepository(database.userDataDao());
    }

    @Singleton
    @Provides
    ServiceDataRepository provideServiceDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new ServiceDataRepository(database.serviceDao());
    }

    @Singleton
    @Provides
    CurrentUserDataRepository provideCurrentUserDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new CurrentUserDataRepository(database.currentUserDao());
    }

    @Singleton
    @Provides
    SettingsDataRepository provideSettingsDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new SettingsDataRepository(database.settingsDao());
    }

    @Singleton
    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(
            UserDataRepository userDataSource,
            ServiceDataRepository serviceSource,
            CurrentUserDataRepository currentUserSource,
            SettingsDataRepository settingsSource,
            Executor executor) {
        return new PriVELTViewModelFactory(userDataSource, serviceSource, currentUserSource, settingsSource, executor);
    }
}
