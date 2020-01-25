package com.kent.university.privelt.database.injections;

import android.content.Context;

import com.kent.university.privelt.database.PriVELTDatabase;
import com.kent.university.privelt.repositories.CredentialsDataRepository;
import com.kent.university.privelt.repositories.ServiceDataRepository;
import com.kent.university.privelt.repositories.UserDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static CredentialsDataRepository provideCredentialsDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new CredentialsDataRepository(database.credentialsDao());
    }

    public static UserDataRepository provideUserDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new UserDataRepository(database.userDataDao());
    }

    public static ServiceDataRepository provideServiceDataSource(Context context) {
        PriVELTDatabase database = PriVELTDatabase.getInstance(context);
        return new ServiceDataRepository(database.serviceDao());
    }

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        CredentialsDataRepository credentialsDataSource = provideCredentialsDataSource(context);
        UserDataRepository userDataSource = provideUserDataSource(context);
        ServiceDataRepository serviceSource = provideServiceDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(credentialsDataSource, userDataSource, serviceSource, executor);
    }
}
