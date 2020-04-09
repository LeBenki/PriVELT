/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import butterknife.ButterKnife
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.api.ServiceHelper
import com.kent.university.privelt.di.DaggerPriVELTComponent
import com.kent.university.privelt.di.RoomModule

abstract class BaseFragment : Fragment() {
    protected abstract val fragmentLayout: Int
    protected abstract fun configureViewModel()
    protected abstract fun configureDesign()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(fragmentLayout, container, false)

        // Using the ButterKnife library
        ButterKnife.bind(this, view)

        // ViewModel
        configureViewModel()
        configureDesign()
        return view
    }

    protected fun <T : ViewModel?> getViewModel(className: Class<T>?): T {
        // Component
        val component = DaggerPriVELTComponent.builder().roomModule(RoomModule(context!!)).build()

        // ViewModelFactory
        val factory = component.viewModelFactory
        return ViewModelProvider(this, factory!!).get(className)
    }

    protected val serviceHelper: ServiceHelper?
        protected get() = (context!!.applicationContext as PriVELTApplication).serviceHelper
}