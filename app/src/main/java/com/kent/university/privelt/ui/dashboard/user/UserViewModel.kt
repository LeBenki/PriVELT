/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kent.university.privelt.model.CurrentUser
import com.kent.university.privelt.repositories.CurrentUserDataRepository
import java.util.concurrent.Executor

class UserViewModel(private val mCurrentUserRepository: CurrentUserDataRepository, private val mExecutor: Executor) : ViewModel() {
    var currentUser: LiveData<CurrentUser>? = null
        private set

    fun init() {
        if (currentUser == null) currentUser = mCurrentUserRepository.currentUser
    }

    fun updateCurrentUser(vararg currentUser: CurrentUser?) {
        mExecutor.execute { mCurrentUserRepository.updateCurrentUser(*currentUser) }
    }

}