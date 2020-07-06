/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.api.services.drive.DriveScopes
import com.kent.university.privelt.R
import com.kent.university.privelt.base.GoogleDriveActivity
import com.kent.university.privelt.base.GoogleDriveListener
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.Settings
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity
import com.privelt.pda.dataplatform.DataPlatformFactory
import com.privelt.pda.dataplatform.DataPlatformType
import com.privelt.pda.dataplatform.generic.Credentials
import com.privelt.pda.dataplatform.hat.HatPlatform
import com.privelt.pda.dataplatform.hat.files.HatFileDetails
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : GoogleDriveActivity(), WebViewDialog.AuthenticationListener {

    override val activityLayout: Int
        get() = R.layout.activity_settings
    private var settingsViewModel: SettingsViewModel? = null
    private var settings: Settings? = null

    //TODO Here we need a recyclerview with each service and a ServiceManager
    override fun configureDesign(savedInstanceState: Bundle?) {
        change_password!!.setOnClickListener {
            val intent = Intent(this, MasterPasswordActivity::class.java)
            intent.putExtra(ARG_CHANGE_PASSWORD, true)
            startActivity(intent)
        }
        logout!!.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle(R.string.log_out)
                    .setMessage(R.string.log_out_confirmation)
                    .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                        startActivity(Intent(this, MasterPasswordActivity::class.java))
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
        listener = object : GoogleDriveListener {
            override fun onDownloadSuccess() {}
            override fun onDownloadFailure() {}
            override fun onConnectionSuccess() {
                val account = GoogleSignIn.getLastSignedInAccount(this@SettingsActivity)
                googleId!!.text = resources.getString(R.string.logged_with, account!!.displayName)
                disconnect!!.visibility = View.VISIBLE
                googleId!!.visibility = View.VISIBLE
            }
        }
        drive!!.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            settings!!.isGoogleDriveAutoSave = b
            if (b) googleDriveConnection()
            settingsViewModel!!.updateSettings(settings)
        }
        fileIdEditText?.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied text", fileIdEditText?.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this@SettingsActivity, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
        disconnect!!.setOnClickListener {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
            googleSignInClient.signOut()
            settings!!.isGoogleDriveAutoSave = false
            settingsViewModel!!.updateSettings(settings)
        }
        hatSwitch.setOnClickListener {
            val newFragment = WebViewDialog(hatEmail.text.toString())
            newFragment.show(supportFragmentManager, "WebViewDialog")
        }
        configureViewModel()
        getSettings()
    }

    private fun getSettings() {
        settingsViewModel!!.settings?.observe(this, Observer { settings: Settings? -> updateSettings(settings) })
    }

    private fun updateSettings(settings: Settings?) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        this.settings = settings
        if (settings == null) this.settings = Settings(false, "")
        drive!!.isChecked = this.settings!!.isGoogleDriveAutoSave
        fileIdEditText?.setText(this.settings!!.googleDriveFileID)
        if (account != null) {
            googleId!!.text = resources.getString(R.string.logged_with, account.displayName)
            disconnect!!.visibility = View.VISIBLE
            googleId!!.visibility = View.VISIBLE
        } else {
            this.settings!!.isGoogleDriveAutoSave = false
            disconnect!!.visibility = View.GONE
            googleId!!.visibility = View.GONE
        }
    }

    override fun configureViewModel() {
        settingsViewModel = getViewModel(SettingsViewModel::class.java)
        settingsViewModel?.init()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.help_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.help) {
            Snackbar.make(logout!!, R.string.save_data, Snackbar.LENGTH_LONG).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ARG_CHANGE_PASSWORD = "ARG_CHANGE_PASSWORD"
    }

    override fun onSuccess(token: String) {
        Toast.makeText(this, token, Toast.LENGTH_LONG).show()

        val credentials = Credentials(token)
        val dataPlatformType = DataPlatformType.HAT

        val dataPlatform = DataPlatformFactory.getDataPlatform(dataPlatformType, credentials)
        val file = getDatabasePath(PriVELTDatabase.PriVELTDatabaseName)

        val hatFilesOps = (dataPlatform as HatPlatform).hatFilesOps

        val hatFileDetails = HatFileDetails(file.name, "app-112-dev", file.absolutePath)

        hatFilesOps.upload(hatFileDetails)
    }

    override fun onFailure(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}