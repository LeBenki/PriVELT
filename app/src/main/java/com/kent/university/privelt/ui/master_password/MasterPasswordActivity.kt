/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.master_password

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.kent.university.privelt.R
import com.kent.university.privelt.base.GoogleDriveActivity
import com.kent.university.privelt.base.GoogleDriveListener
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.ui.dashboard.DashboardActivity
import com.kent.university.privelt.ui.settings.SettingsActivity
import com.kent.university.privelt.utils.EyePassword
import com.kent.university.privelt.utils.PasswordChecker
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.android.synthetic.main.activity_main_password.*

class MasterPasswordActivity : GoogleDriveActivity(), View.OnClickListener, TextWatcher {
    private var zxcvbn: Zxcvbn? = null
    private var changePassword = false

    private var masterPasswordAlreadyGiven = false

    override val activityLayout: Int
        get() = R.layout.activity_main_password

    override fun configureViewModel() {}
    private fun configureLoginScreen() {
        reset!!.setText(R.string.reset_data)
        resetMasterPassword()
        password_meter!!.visibility = View.GONE
        hint!!.visibility = View.GONE
        confirm_password!!.visibility = View.GONE
        eye_confirm_password!!.visibility = View.GONE
        progress_circular!!.visibility = View.GONE
        reset!!.isEnabled = true
        start!!.isEnabled = true
    }

    private fun configureNewPasswordScreen() {
        reset!!.setText(R.string.import_your_data)
        progress_circular!!.visibility = View.GONE
        reset!!.isEnabled = true
        start!!.isEnabled = true
        password_meter!!.visibility = View.VISIBLE
        hint!!.visibility = View.VISIBLE
        confirm_password!!.visibility = View.VISIBLE
        eye_confirm_password!!.visibility = View.VISIBLE
        reset!!.setText(R.string.import_your_data)
        onDataImported()
        onDataImported()
    }

    override fun configureDesign(savedInstanceState: Bundle?) {
        start!!.setOnClickListener(this)
        password!!.addTextChangedListener(this)
        zxcvbn = Zxcvbn()
        if (intent != null) {
            changePassword = intent.getBooleanExtra(SettingsActivity.ARG_CHANGE_PASSWORD, false)
        } else if (savedInstanceState != null) {
            changePassword = savedInstanceState.getBoolean(SettingsActivity.ARG_CHANGE_PASSWORD, false)
        }
        title = ""
        if (changePassword) {
            start!!.text = getString(R.string.change_master_password)
        } else supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        masterPasswordAlreadyGiven = getDatabasePath(PriVELTDatabase.PriVELTDatabaseName).exists()
        if (!masterPasswordAlreadyGiven || changePassword) {
            configureNewPasswordScreen()
        } else {
            configureLoginScreen()
        }
        EyePassword.configureEye(eye_password!!, password!!)
        EyePassword.configureEye(eye_confirm_password!!, confirm_password!!)
        listener = object : GoogleDriveListener {
            override fun onDownloadSuccess() {
                Toast.makeText(this@MasterPasswordActivity, R.string.data_imported_correctly, Toast.LENGTH_LONG).show()
                configureLoginScreen()
                resetMasterPassword()
            }

            override fun onDownloadFailure() {
                Toast.makeText(this@MasterPasswordActivity, R.string.error_occurred, Toast.LENGTH_LONG).show()
            }

            override fun onConnectionSuccess() {}
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SettingsActivity.ARG_CHANGE_PASSWORD, changePassword)
    }

    @SuppressLint("StaticFieldLeak")
    private fun resetMasterPassword() {
        reset!!.setOnClickListener { view: View? ->
            AlertDialog.Builder(this)
                    .setTitle(R.string.reset_confirmation)
                    .setMessage(R.string.process_confirmation)
                    .setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int ->
                        reset!!.isEnabled = false
                        start!!.isEnabled = false
                        progress_circular!!.visibility = View.VISIBLE
                        object : AsyncTask<Void?, Void?, Void?>() {
                             override fun doInBackground(vararg voids: Void?): Void? {
                                masterPasswordAlreadyGiven = false
                                changePassword = false
                                deleteDatabase(PriVELTDatabase.PriVELTDatabaseName)
                                return null
                             }

                            override fun onPostExecute(aVoid: Void?) {
                                super.onPostExecute(aVoid)
                                configureNewPasswordScreen()
                                Toast.makeText(this@MasterPasswordActivity, R.string.reset_done, Toast.LENGTH_LONG).show()
                            }
                        }.execute()
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
    }

    @SuppressLint("StaticFieldLeak")
    override fun onClick(view: View) {
        if (confirm_password!!.visibility == View.VISIBLE && password!!.text.toString() != confirm_password!!.text.toString()) {
            Toast.makeText(this@MasterPasswordActivity, R.string.different_password, Toast.LENGTH_LONG).show()
            return
        }
        if (!PasswordChecker.checkPassword(zxcvbn!!, password!!.text.toString())) {
            val errMessage = if (changePassword || !masterPasswordAlreadyGiven) R.string.not_respect_policy else R.string.wrong_password
            Toast.makeText(this@MasterPasswordActivity, errMessage, Toast.LENGTH_LONG).show()
            return
        }
        start!!.isEnabled = false
        reset!!.isEnabled = false
        progress_circular!!.visibility = View.VISIBLE
        val password = password!!.text
        identityManager?.password = password
        object : AsyncTask<Void?, Void?, Boolean>() {
             override fun doInBackground(vararg v: Void?): Boolean {
                return if (changePassword || !masterPasswordAlreadyGiven) {
                    if (changePassword) {
                        this@MasterPasswordActivity.identityManager?.changePassword(password)
                    }
                    masterPasswordAlreadyGiven = true
                    true
                } else {
                    val dbHelperObj = PriVELTDatabase.getInstance(this@MasterPasswordActivity)
                    try {
                        dbHelperObj?.serviceDao()?.allServices
                    } catch (e: Exception) {
                        dbHelperObj?.close()
                        PriVELTDatabase.nullDatabase()
                        return false
                    }
                    true
                }
            }

            override fun onPostExecute(res: Boolean) {
                super.onPostExecute(res)
                start!!.isEnabled = true
                reset!!.isEnabled = true
                progress_circular!!.visibility = View.GONE
                if (res) {
                    if (!changePassword) {
                        startActivity(Intent(this@MasterPasswordActivity, DashboardActivity::class.java))
                    }
                    finish()
                } else {
                    val errMessage = if (changePassword) R.string.same_password else R.string.wrong_password
                    Toast.makeText(this@MasterPasswordActivity, errMessage, Toast.LENGTH_LONG).show()
                }
            }
        }.execute()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        updatePasswordStrengthView(editable.toString())
    }

    fun onDataImported() {
        reset!!.setOnClickListener { v: View? ->
            val alert = AlertDialog.Builder(this)
            val edittext = EditText(this)
            edittext.setTextColor(Color.parseColor("#000000"))
            alert.setTitle(R.string.enter_file_id)
            alert.setView(edittext)
            alert.setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int -> googleDriveConnectionAndDownload(true, edittext.text.toString()) }
            alert.show()
        }
    }

    private fun updatePasswordStrengthView(password: String) {
        if (TextView.VISIBLE != password_strength!!.visibility) return
        if (password.isEmpty()) {
            password_strength!!.text = ""
            progress_password!!.progress = 0
            return
        }
        val strength = zxcvbn!!.measure(password)
        val ps = PasswordStrength.values()[strength.score]
        progress_password!!.progress = ps.progress
        password_strength!!.setText(ps.resId)
        password_strength!!.setTextColor(ps.color)
        progress_password!!.progressDrawable.setColorFilter(ps.color, PorterDuff.Mode.SRC_IN)
    }
}