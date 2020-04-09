/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.di

import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RoomModule::class])
interface PriVELTComponent {
    val viewModelFactory: PriVELTViewModelFactory?
}