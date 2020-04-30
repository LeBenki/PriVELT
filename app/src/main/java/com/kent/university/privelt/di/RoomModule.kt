/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.repositories.*
import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class RoomModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSensorStatusSource(context: Context?): SensorStatusRepository {
        val database = PriVELTDatabase.getInstance(context!!)
        return SensorStatusRepository(database?.sensorStatusDao()!!)
    }

    @Singleton
    @Provides
    fun provideUserDataSource(context: Context?): UserDataRepository {
        val database = PriVELTDatabase.getInstance(context!!)
        return UserDataRepository(database?.userDataDao()!!)
    }

    @Singleton
    @Provides
    fun provideServiceDataSource(context: Context?): ServiceDataRepository {
        val database = PriVELTDatabase.getInstance(context!!)
        return ServiceDataRepository(database?.serviceDao()!!)
    }

    @Singleton
    @Provides
    fun provideCurrentUserDataSource(context: Context?): CurrentUserDataRepository {
        val database = PriVELTDatabase.getInstance(context!!)
        return CurrentUserDataRepository(database?.currentUserDao()!!)
    }

    @Singleton
    @Provides
    fun provideSettingsDataSource(context: Context?): SettingsDataRepository {
        val database = PriVELTDatabase.getInstance(context!!)
        return SettingsDataRepository(database?.settingsDao()!!)
    }

    @Singleton
    @Provides
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideViewModelFactory(
            userDataSource: UserDataRepository?,
            serviceSource: ServiceDataRepository?,
            currentUserSource: CurrentUserDataRepository?,
            settingsSource: SettingsDataRepository?,
            sensorStatusSource: SensorStatusRepository?,
            executor: Executor?): ViewModelProvider.Factory {
        return PriVELTViewModelFactory(userDataSource!!, serviceSource!!, currentUserSource!!, settingsSource!!, sensorStatusSource!!, executor!!)
    }

}