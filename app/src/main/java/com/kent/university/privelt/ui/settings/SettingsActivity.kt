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
import android.widget.*
import androidx.lifecycle.Observer
import butterknife.BindView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.api.services.drive.DriveScopes
import com.kent.university.privelt.R
import com.kent.university.privelt.base.GoogleDriveActivity
import com.kent.university.privelt.base.GoogleDriveListener
import com.kent.university.privelt.model.Settings
import com.kent.university.privelt.ui.master_password.MasterPasswordActivity
import com.kent.university.privelt.ui.settings.SettingsActivity

class SettingsActivity : GoogleDriveActivity() {
    @JvmField
    @BindView(R.id.change_password)
    var changePassword: Button? = null

    @JvmField
    @BindView(R.id.logout)
    var logout: Button? = null

    @JvmField
    @BindView(R.id.drive)
    var drive: Switch? = null

    @JvmField
    @BindView(R.id.fileId)
    var fileId: EditText? = null

    @JvmField
    @BindView(R.id.googleId)
    var googleId: TextView? = null

    @JvmField
    @BindView(R.id.disconnect)
    var disconnect: ImageView? = null
    var settingsViewModel: SettingsViewModel? = null
    private var settings: Settings? = null
    override fun configureDesign(savedInstanceState: Bundle?) {
        changePassword!!.setOnClickListener { view: View? ->
            val intent = Intent(this, MasterPasswordActivity::class.java)
            intent.putExtra(ARG_CHANGE_PASSWORD, true)
            startActivity(intent)
        }
        logout!!.setOnClickListener { view: View? ->
            AlertDialog.Builder(this)
                    .setTitle(R.string.log_out)
                    .setMessage(R.string.log_out_confirmation)
                    .setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int ->
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
        drive!!.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            settings!!.isGoogleDriveAutoSave = b
            if (b) googleDriveConnection()
            settingsViewModel!!.updateSettings(settings)
        }
        fileId.setOnClickListener(View.OnClickListener { v: View? ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied text", fileId.getText().toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this@SettingsActivity, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        })
        disconnect!!.setOnClickListener { view: View? ->
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
            googleSignInClient.signOut()
            settings!!.isGoogleDriveAutoSave = false
            settingsViewModel!!.updateSettings(settings)
        }
        configureViewModel()
        getSettings()
    }

    private fun getSettings() {
        settingsViewModel!!.settings.observe(this, Observer { settings: Settings? -> updateSettings(settings) })
    }

    private fun updateSettings(settings: Settings?) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        this.settings = settings
        if (settings == null) this.settings = Settings(false, "")
        drive!!.isChecked = this.settings!!.isGoogleDriveAutoSave
        fileId.setText(this.settings!!.googleDriveFileID)
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

    override fun getActivityLayout(): Int {
        return R.layout.activity_settings
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
}