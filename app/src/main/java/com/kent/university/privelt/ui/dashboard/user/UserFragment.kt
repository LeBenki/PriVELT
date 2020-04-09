/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard.user

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.Editable
import android.util.Pair
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.ValueCallback
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.OnClick
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseFragment
import com.kent.university.privelt.model.CurrentUser
import com.kent.university.privelt.ui.dashboard.user.UserTextWatcher.MyTextWatcher
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.util.*

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
internal annotation class SetterMethod(val method: String = "")
class UserFragment : BaseFragment(), MyTextWatcher {
    @JvmField
    @BindView(R.id.first_name)
    @SetterMethod(method = "setFirstName")
    var firstName: EditText? = null

    @JvmField
    @BindView(R.id.last_name)
    @SetterMethod(method = "setLastName")
    var lastName: EditText? = null

    @JvmField
    @BindView(R.id.birthday)
    @SetterMethod(method = "setBirthday")
    var birthday: EditText? = null

    @JvmField
    @BindView(R.id.address)
    @SetterMethod(method = "setAddress")
    var address: EditText? = null

    @JvmField
    @BindView(R.id.phone_number)
    @SetterMethod(method = "setPhoneNumber")
    var phoneNumber: EditText? = null

    @JvmField
    @BindView(R.id.mail)
    @SetterMethod(method = "setMail")
    var mail: EditText? = null

    @JvmField
    @BindView(R.id.first_name_tv)
    @SetterMethod(method = "setFirstName")
    var firstNameTv: TextView? = null

    @JvmField
    @BindView(R.id.last_name_tv)
    @SetterMethod(method = "setLastName")
    var lastNameTv: TextView? = null

    @JvmField
    @BindView(R.id.birthday_tv)
    @SetterMethod(method = "setBirthday")
    var birthdayTv: TextView? = null

    @JvmField
    @BindView(R.id.address_tv)
    @SetterMethod(method = "setAddress")
    var addressTv: TextView? = null

    @JvmField
    @BindView(R.id.phone_number_tv)
    @SetterMethod(method = "setPhoneNumber")
    var phoneNumberTv: TextView? = null

    @JvmField
    @BindView(R.id.mail_tv)
    @SetterMethod(method = "setMail")
    var mailTv: TextView? = null

    @JvmField
    @BindView(R.id.edit_texts)
    var editTexts: LinearLayout? = null

    @JvmField
    @BindView(R.id.text_views)
    var textViews: LinearLayout? = null
    private var userViewModel: UserViewModel? = null
    private var currentUser: CurrentUser? = null
    private var mOptionsMenu: Menu? = null

    @OnClick(R.id.birthday)
    fun onBirthdayClick() {
        val listener = OnDateSetListener { datePicker: DatePicker?, dayOfMonth: Int, monthOfYear: Int, year: Int ->
            birthday!!.setText(StringBuilder()
                    .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "))
        }
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dialog = DatePickerDialog(context!!, listener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])
        dialog.show()
    }

    private fun configureEditTexts() {
        editTexts!!.visibility = View.INVISIBLE
        onFieldsAction(ValueCallback { s: Pair<Field, TextView> -> if (s.second is EditText) s.second.addTextChangedListener(UserTextWatcher(s.second as EditText, this)) })
    }

    override fun getFragmentLayout(): Int {
        return R.layout.fragment_user
    }

    override fun configureViewModel() {
        userViewModel = getViewModel(UserViewModel::class.java)
        userViewModel?.init()
    }

    override fun configureDesign() {
        getCurrentUser()
        configureEditTexts()
    }

    private fun getCurrentUser() {
        userViewModel!!.currentUser.observe(this, Observer { currentUser: CurrentUser? -> updateCurrentUser(currentUser) })
    }

    private fun updateCurrentUser(currentUser: CurrentUser?) {
        this.currentUser = currentUser
        if (currentUser == null) this.currentUser = CurrentUser("", "", "", "", "", "")
        onFieldsAction(ValueCallback { pair: Pair<Field, TextView> ->
            val m: String = pair.first.getAnnotation(SetterMethod::class.java).method().replaceFirst("s".toRegex(), "g")
            for (method in this.currentUser!!.javaClass.methods) {
                if (method.name.equals(m, ignoreCase = true)) {
                    try {
                        pair.second.text = method.invoke(this.currentUser) as CharSequence
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun afterTextChanged(editText: EditText, editable: Editable) {
        onFieldsAction(ValueCallback { pair: Pair<Field, TextView> ->
            if (currentUser == null) return@onFieldsAction
            val m: String = pair.first.getAnnotation(SetterMethod::class.java).method()
            for (method in currentUser!!.javaClass.methods) {
                if (method.name.equals(m, ignoreCase = true)) {
                    try {
                        method.invoke(currentUser, editable.toString())
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }
                }
            }
        }, editText)
    }

    private fun onFieldsAction(callback: ValueCallback<Pair<Field, TextView>>, vararg objects: Any) {
        val fields = javaClass.declaredFields
        for (field in fields) {
            if (!field.isAnnotationPresent(SetterMethod::class.java)) continue
            val setterMethod = field.getAnnotation(SetterMethod::class.java)
            if (setterMethod == null || setterMethod.method() == "") continue
            try {
                if (objects.size > 0 && field[this] === objects[0] || objects.size == 0) callback.onReceiveValue(Pair(field, field[this] as TextView))
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit) {
            textViews!!.visibility = View.INVISIBLE
            editTexts!!.visibility = View.VISIBLE
            mOptionsMenu!!.findItem(R.id.edit).isVisible = false
            mOptionsMenu!!.findItem(R.id.check).isVisible = true
            mOptionsMenu!!.findItem(R.id.settings).isVisible = false
        } else if (item.itemId == R.id.check) {
            textViews!!.visibility = View.VISIBLE
            editTexts!!.visibility = View.INVISIBLE
            mOptionsMenu!!.findItem(R.id.edit).isVisible = true
            mOptionsMenu!!.findItem(R.id.check).isVisible = false
            mOptionsMenu!!.findItem(R.id.settings).isVisible = true
            userViewModel!!.updateCurrentUser(currentUser)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        mOptionsMenu = menu
    }
}