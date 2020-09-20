/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.user

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kent.university.privelt.R
import com.kent.university.privelt.di.DaggerPriVELTComponent
import com.kent.university.privelt.di.RoomModule
import com.kent.university.privelt.model.CurrentUser
import kotlinx.android.synthetic.main.fragment_user_edit.view.*
import kotlinx.android.synthetic.main.fragment_user_display.view.*
import java.util.*

class UserFragment : Fragment() {
    private var userViewModel: UserViewModel? = null
    private var currentUser: CurrentUser? = null

    private var edit: MenuItem? = null
    private var save: MenuItem? = null

    private fun onBirthdayClick() {
        val listener = OnDateSetListener { _: DatePicker?, dayOfMonth: Int, monthOfYear: Int, year: Int ->
            view?.birthDate!!.setText(StringBuilder()
                    .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "))
        }
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dialog = DatePickerDialog(context!!, listener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])
        dialog.show()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Component
        val component = DaggerPriVELTComponent.builder().roomModule(RoomModule(context!!)).build()

        // ViewModelFactory
        val factory = component.viewModelFactory
        userViewModel =  ViewModelProvider(this, factory!!).get(UserViewModel::class.java)
        userViewModel?.init()
        
        getCurrentUser()

        val binding = inflater.inflate(
                R.layout.fragment_user,
                container,
                false
        )

        userViewModel!!.getIsEditMode().observe(this, Observer { bool: Boolean ->
            val display = view?.findViewById<View>(R.id.user_display)
            val edit = view?.findViewById<View>(R.id.user_edit)

            if (bool) {
                display?.visibility = View.GONE
                edit?.visibility = View.VISIBLE
                save?.isVisible = true
                this.edit?.isVisible = false
            }
            else {
                display?.visibility = View.VISIBLE
                edit?.visibility = View.GONE
                save?.isVisible = false
                this.edit?.isVisible = true
            }
        })

        view?.birthDate?.setOnClickListener {onBirthdayClick()}

        return binding
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
        view?.firstName?.setText(currentUser?.firstName)
        view?.lastName?.setText(currentUser?.lastName)
        view?.birthDate?.setText(currentUser?.birthday)
        view?.address?.setText(currentUser?.address)
        view?.email?.setText(currentUser?.mail)
        view?.phoneNumber?.setText(currentUser?.phoneNumber)

        view?.firstNameText?.text = currentUser?.firstName
        view?.lastNameText?.text = currentUser?.lastName
        view?.birthdayText?.text = currentUser?.birthday
        view?.addressText?.text = currentUser?.address
        view?.emailText?.text = currentUser?.mail
        view?.phoneText?.text = currentUser?.phoneNumber
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.user, menu)

        save = menu.findItem(R.id.menu_save)
        edit = menu.findItem(R.id.menu_edit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                userViewModel?.changeEditMode(false)
                saveUser()
                true
            }
            R.id.menu_edit -> {
                userViewModel?.changeEditMode(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveUser() {
        val user = CurrentUser(
                view?.firstName?.text.toString(),
                view?.lastName?.text.toString(),
                view?.birthDate?.text.toString(),
                view?.address?.text.toString(),
                view?.email?.text.toString(),
                view?.phoneNumber?.text.toString()
        )
        userViewModel?.updateCurrentUser(user)
    }

    override fun onPause() {
        super.onPause()
        userViewModel?.changeEditMode(false)
    }

}