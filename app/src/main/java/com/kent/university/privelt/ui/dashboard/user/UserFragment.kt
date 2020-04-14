/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.user

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.Observer
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.model.CurrentUser
import kotlinx.android.synthetic.main.fragment_user.view.*
import java.util.*

class UserFragment : BaseFragment() {
    private var userViewModel: UserViewModel? = null
    private var currentUser: CurrentUser? = null
    private var mOptionsMenu: Menu? = null

    private fun onBirthdayClick() {
        val listener = OnDateSetListener { _: DatePicker?, dayOfMonth: Int, monthOfYear: Int, year: Int ->
            baseView.birthday!!.setText(StringBuilder()
                    .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "))
        }
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dialog = DatePickerDialog(context!!, listener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])
        dialog.show()
    }

    override val fragmentLayout: Int
        get() = R.layout.fragment_user

    override fun configureDesign(view: View) {
        getCurrentUser()

        baseView.birthday.setOnClickListener {onBirthdayClick()}
    }

    private fun getCurrentUser() {
        userViewModel?.currentUser?.observe(this, Observer { userData: CurrentUser? -> updateUserData(userData) })
    }

    private fun updateUserData(currentUser: CurrentUser?) {
        if (currentUser == null) {
            this.currentUser = CurrentUser("", "", "", "", "", "")
        }
        else
            this.currentUser = currentUser

        baseView.first_name.setText(currentUser?.firstName)
        baseView.last_name.setText(currentUser?.lastName)
        baseView.birthday.setText(currentUser?.birthday)
        baseView.email.setText(currentUser?.mail)
        baseView.phone_number.setText(currentUser?.phoneNumber)
        baseView.address.setText(currentUser?.address)
    }

    override fun configureViewModel() {
        userViewModel = getViewModel(UserViewModel::class.java)
        userViewModel?.init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()

        currentUser?.firstName = baseView.first_name.text.toString()
        currentUser?.lastName = baseView.last_name.text.toString()
        currentUser?.birthday = baseView.birthday.text.toString()
        currentUser?.address = baseView.address.text.toString()
        currentUser?.mail = baseView.email.text.toString()
        currentUser?.phoneNumber = baseView.phone_number.text.toString()

        userViewModel?.updateCurrentUser(currentUser)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        mOptionsMenu = menu
    }
}