/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.api.PasswordManager
import com.kent.university.privelt.di.DaggerPriVELTComponent
import com.kent.university.privelt.di.RoomModule

abstract class BaseActivity : AppCompatActivity() {
    protected abstract val activityLayout: Int
    protected abstract fun configureViewModel()
    protected abstract fun configureDesign(savedInstanceState: Bundle?)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Associates the layout file to this class
        this.setContentView(activityLayout)

        configureViewModel()
        configureDesign(savedInstanceState)
    }

    protected fun <T : ViewModel?> getViewModel(className: Class<T>): T {
        // Component
        val component = DaggerPriVELTComponent.builder().roomModule(RoomModule(this)).build()

        // ViewModelFactory
        val factory = component.viewModelFactory
        return ViewModelProvider(this, factory!!).get(className)
    }

    protected val identityManager: PasswordManager?
        get() = (application as PriVELTApplication).identityManager

    override fun onResume() {
        super.onResume()
        (applicationContext as PriVELTApplication).currentActivity = this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}