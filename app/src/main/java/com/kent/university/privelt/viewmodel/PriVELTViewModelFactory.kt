/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kent.university.privelt.repositories.CurrentUserDataRepository
import com.kent.university.privelt.repositories.ServiceDataRepository
import com.kent.university.privelt.repositories.SettingsDataRepository
import com.kent.university.privelt.repositories.UserDataRepository
import com.kent.university.privelt.ui.dashboard.card.CardViewModel
import com.kent.university.privelt.ui.dashboard.card.detailed.DetailedCardViewModel
import com.kent.university.privelt.ui.dashboard.user.UserViewModel
import com.kent.university.privelt.ui.data.DataViewModel
import com.kent.university.privelt.ui.risk_value.RiskValueViewModel
import com.kent.university.privelt.ui.settings.SettingsViewModel
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriVELTViewModelFactory @Inject constructor(var mUserDataSource: UserDataRepository, var mServiceDataSource: ServiceDataRepository, var mCurrentUserDataRepository: CurrentUserDataRepository, var mSettingsDataSource: SettingsDataRepository, var mExecutor: Executor) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            if (CardViewModel::class.java == modelClass) {
                modelClass.getConstructor(ServiceDataRepository::class.java,
                                UserDataRepository::class.java,
                                Executor::class.java)
                        .newInstance(mServiceDataSource, mUserDataSource, mExecutor)
            } else if (DataViewModel::class.java == modelClass) {
                modelClass.getConstructor(UserDataRepository::class.java,
                                ServiceDataRepository::class.java,
                                Executor::class.java)
                        .newInstance(mUserDataSource, mServiceDataSource, mExecutor)
            } else if (RiskValueViewModel::class.java == modelClass) {
                modelClass.getConstructor(ServiceDataRepository::class.java,
                                UserDataRepository::class.java)
                        .newInstance(mServiceDataSource, mUserDataSource)
            } else if (UserViewModel::class.java == modelClass) {
                modelClass.getConstructor(CurrentUserDataRepository::class.java,
                                Executor::class.java)
                        .newInstance(mCurrentUserDataRepository, mExecutor)
            } else if (DetailedCardViewModel::class.java == modelClass) {
                modelClass.getConstructor(UserDataRepository::class.java,
                                Executor::class.java)
                        .newInstance(mUserDataSource, mExecutor)
            } else if (SettingsViewModel::class.java == modelClass) {
                modelClass.getConstructor(SettingsDataRepository::class.java,
                                Executor::class.java)
                        .newInstance(mSettingsDataSource, mExecutor)
            } else throw IllegalArgumentException("Unknown ViewModel class")
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException("Unknown ViewModel class")
        } catch (e: InstantiationException) {
            throw IllegalArgumentException("Unknown ViewModel class")
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException("Unknown ViewModel class")
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}