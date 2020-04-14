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
class PriVELTViewModelFactory @Inject constructor(private var mUserDataSource: UserDataRepository, private var mServiceDataSource: ServiceDataRepository, private var mCurrentUserDataRepository: CurrentUserDataRepository, private var mSettingsDataSource: SettingsDataRepository, private var mExecutor: Executor) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            when {
                CardViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(ServiceDataRepository::class.java,
                                    UserDataRepository::class.java,
                                    Executor::class.java)
                            .newInstance(mServiceDataSource, mUserDataSource, mExecutor) as T
                }
                DataViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(UserDataRepository::class.java,
                                    ServiceDataRepository::class.java)
                            .newInstance(mUserDataSource, mServiceDataSource) as T
                }
                RiskValueViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(ServiceDataRepository::class.java,
                                    UserDataRepository::class.java)
                            .newInstance(mServiceDataSource, mUserDataSource) as T
                }
                UserViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(CurrentUserDataRepository::class.java,
                                    Executor::class.java)
                            .newInstance(mCurrentUserDataRepository, mExecutor) as T
                }
                DetailedCardViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(UserDataRepository::class.java,
                                    Executor::class.java)
                            .newInstance(mUserDataSource, mExecutor) as T
                }
                SettingsViewModel::class.java == modelClass -> {
                    modelClass.getConstructor(SettingsDataRepository::class.java,
                                    Executor::class.java)
                            .newInstance(mSettingsDataSource, mExecutor) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
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