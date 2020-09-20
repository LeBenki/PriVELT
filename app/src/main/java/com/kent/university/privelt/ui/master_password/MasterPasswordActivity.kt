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
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.kent.university.privelt.R
import com.kent.university.privelt.base.PDAActivity
import com.kent.university.privelt.base.PDAListener
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.ServicePDA
import com.kent.university.privelt.ui.dashboard.DashboardActivity
import com.kent.university.privelt.ui.settings.SettingsActivity
import com.kent.university.privelt.utils.EyePassword
import com.kent.university.privelt.utils.PasswordChecker
import com.kent.university.privelt.utils.biometric.BiometricPromptTinkManager
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.android.synthetic.main.activity_main_password.*


class MasterPasswordActivity : PDAActivity(), View.OnClickListener, TextWatcher, ImportDataAdapter.ImportDataListener {
    private var editTextParam: String? = null
    private var zxcvbn: Zxcvbn? = null
    private var changePassword = false
    private lateinit var biometricPromptManager: BiometricPromptTinkManager

    private var masterPasswordAlreadyGiven = false

    private var pdaDialog: ImportDataDialog? = null

    override val activityLayout: Int
        get() = R.layout.activity_main_password

    override fun configureViewModel() {

    }

    private fun configureLoginScreen() {
        reset!!.setText(R.string.reset_data)
        reset.paintFlags = reset.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        resetMasterPassword()
        password_meter!!.visibility = View.GONE
        hint!!.visibility = View.GONE
        choose_password!!.visibility = View.GONE
        confirm_password!!.visibility = View.GONE
        eye_confirm_password!!.visibility = View.GONE
        progress_circular!!.visibility = View.GONE
        reset!!.isEnabled = true
        start!!.isEnabled = true
        if (biometricPromptManager.checkIfPreviousEncryptedData() && biometricPromptManager.isFingerPrintAvailable())
            fingerprint!!.visibility = View.VISIBLE
        else
            fingerprint!!.visibility = View.GONE
    }

    private fun configureNewPasswordScreen() {
        reset!!.setText(R.string.import_your_data)
        reset.paintFlags = reset.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        progress_circular!!.visibility = View.GONE
        choose_password!!.visibility = View.VISIBLE
        reset!!.isEnabled = true
        start!!.isEnabled = true
        password_meter!!.visibility = View.VISIBLE
        hint!!.visibility = View.VISIBLE
        confirm_password!!.visibility = View.VISIBLE
        eye_confirm_password!!.visibility = View.VISIBLE
        reset!!.setText(R.string.import_your_data)
        onDataImported()
        onDataImported()
        fingerprint!!.visibility = View.GONE
    }

    override fun configureDesign(savedInstanceState: Bundle?) {
        biometricPromptManager = BiometricPromptTinkManager(this)

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
        listener = object : PDAListener {
            override fun onDownloadSuccess() {
                Toast.makeText(this@MasterPasswordActivity, R.string.data_imported_correctly, Toast.LENGTH_LONG).show()
                configureLoginScreen()
                resetMasterPassword()
            }

            override fun onDownloadFailure() {
                Toast.makeText(this@MasterPasswordActivity, R.string.error_occurred, Toast.LENGTH_LONG).show()
            }

            override fun onConnectionSuccess() {}
            override fun onHatUploadSuccess(fileId: String) {
            }

            override fun onHatUploadFailure(error: String) {
            }
        }

        fingerprint.setOnClickListener {
            decryptPrompt()
        }

        if (!changePassword && biometricPromptManager.checkIfPreviousEncryptedData() && biometricPromptManager.isFingerPrintAvailable())
            decryptPrompt()
    }

    private fun decryptPrompt() {
        biometricPromptManager.decryptPrompt(
                failedAction = { },
                successAction = {
                    val password = String(it)
                    val editable: Editable = SpannableStringBuilder(password)
                    launchDashboard(editable)
                }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SettingsActivity.ARG_CHANGE_PASSWORD, changePassword)
    }

    @SuppressLint("StaticFieldLeak")
    private fun resetMasterPassword() {
        reset!!.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle(R.string.reset_confirmation)
                    .setMessage(R.string.process_confirmation)
                    .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
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
                                biometricPromptManager.clearMasterPassword()
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
        val pass = password!!.text.toString()
        val password = password!!.text
        identityManager?.password = password
        object : AsyncTask<Void?, Void?, Pair<Boolean, String>>() {
            override fun doInBackground(vararg v: Void?): Pair<Boolean, String> {
                if (changePassword || !masterPasswordAlreadyGiven) {
                    masterPasswordAlreadyGiven = true
                    return Pair(true, pass)
                } else {
                    val dbHelperObj = PriVELTDatabase.getInstance(this@MasterPasswordActivity)
                    try {
                        dbHelperObj?.serviceDao()?.allServices
                        dbHelperObj?.currentUserDao()?.currentUser
                    } catch (e: Exception) {
                        dbHelperObj?.close()
                        PriVELTDatabase.nullDatabase()
                        return Pair(false, pass)
                    }
                    return Pair(true, pass)
                }
            }

            override fun onPostExecute(res: Pair<Boolean, String>) {
                super.onPostExecute(res)
                start!!.isEnabled = true
                reset!!.isEnabled = true
                progress_circular!!.visibility = View.GONE
                if (res.first) {
                    if (changePassword)
                        biometricPromptManager.clearMasterPassword()
                    if (changePassword || !biometricPromptManager.checkIfPreviousEncryptedData() && biometricPromptManager.isFingerPrintAvailable())
                        AlertDialog.Builder(this@MasterPasswordActivity)
                                .setTitle("Fingerprint")
                                .setMessage("Do you want to enable fingerprint to login ?")
                                .setPositiveButton("Yes"
                                ) { _, _ ->
                                    biometricPromptManager.encryptPrompt(
                                            data = res.second.toByteArray(),
                                            failedAction = { },
                                            successAction = {
                                                launchDashboard(password)
                                            }
                                    )
                                }
                                .setNegativeButton("No") { _, _ ->
                                launchDashboard(password)
                                }.show()
                    else
                        launchDashboard(password)
                } else {
                    val errMessage = if (changePassword) R.string.same_password else R.string.wrong_password
                    Toast.makeText(this@MasterPasswordActivity, errMessage, Toast.LENGTH_LONG).show()
                }
            }
        }.execute()
    }

    fun launchDashboard(password: Editable) {
        identityManager?.password = password
        if (!changePassword) {
            startActivity(Intent(this@MasterPasswordActivity, DashboardActivity::class.java))
        } else {
            this@MasterPasswordActivity.identityManager?.changePassword(password)
        }
        finish()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        updatePasswordStrengthView(editable.toString())
    }

    private fun onDataImported() {
        reset!!.setOnClickListener {
            pdaDialog = ImportDataDialog()
            pdaDialog?.show(supportFragmentManager, "ImportDataDialog")
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
        progress_password!!.progressDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ps.color, BlendModeCompat.SRC_IN)
    }

    override fun onPDAClick(servicePDA: ServicePDA) {
        when (servicePDA.title) {
            "HAT" -> processHatClick()
            "Google" -> processGoogleClick()
        }
        pdaDialog?.dismiss()
    }

    private fun processGoogleClick() {
        alerDialogBuilder(R.string.enter_file_id, R.string.file_id, Runnable {
            googleDriveConnectionAndDownload(true, editTextParam!!)
        })
    }

    private fun processHatClick() {
        alerDialogBuilder(R.string.please_enter_email, R.string.email, Runnable {
            loginHAT(editTextParam!!)
        })
    }

    override fun hatLogged() {
        alerDialogBuilder(R.string.please_enter_file_id, R.string.file_id, Runnable {
            downloadFileWithHAT(editTextParam!!)
        })
    }

    //TODO create dialog fragment
    private fun alerDialogBuilder(title: Int, hint: Int, runnable: Runnable) {
        val alert = AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.setTextColor(Color.parseColor("#000000"))
        edittext.hint = getString(hint)
        edittext.setSingleLine()
        val container = FrameLayout(this)
        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        params.topMargin = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        edittext.layoutParams = params
        container.addView(edittext)
        alert.setView(container)
        alert.setTitle(getString(title))
        alert.setPositiveButton(R.string.continue_string) { _: DialogInterface?, _: Int ->
            editTextParam = edittext.text.toString()
            runnable.run()
        }
        alert.show()
    }
}
