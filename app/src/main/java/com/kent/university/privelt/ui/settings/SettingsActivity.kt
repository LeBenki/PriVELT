/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
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
import com.kent.university.privelt.utils.hat.AccountManager
import com.kent.university.privelt.utils.hat.UploadFileTask
import com.privelt.pda.dataplatform.DataPlatformFactory
import com.privelt.pda.dataplatform.DataPlatformType
import com.privelt.pda.dataplatform.generic.Credentials
import com.privelt.pda.dataplatform.generic.DataPlatformClient
import com.privelt.pda.dataplatform.hat.HatClient
import com.privelt.pda.dataplatform.hat.HatPlatform
import com.privelt.pda.dataplatform.hat.files.HatFileDetails
import com.privelt.pda.dataplatform.hat.response.HatAuthenticationResponse
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class SettingsActivity : GoogleDriveActivity(), WebViewDialog.AuthenticationListener {

    override val activityLayout: Int
        get() = R.layout.activity_settings
    private var settingsViewModel: SettingsViewModel? = null
    private var settings: Settings? = null

    private val DEEP_LINK_SCHEME = "https"
    private val DEEP_LINK_DOMAIN = "pdaapi.app"

    private fun getDeepLink(): String? {
        return String.format("%s://%s", DEEP_LINK_SCHEME, DEEP_LINK_DOMAIN)
    }

    private var credentials: Credentials? = null
    private val dataPlatformType: DataPlatformType? = null
    private var dataPlatformClient: DataPlatformClient? = null
    private var hatLogin = false
    private var username = ""
    private var token = ""

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
        configureHat()
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

        val alert = AlertDialog.Builder(this)
        val edittext = EditText(this)
        edittext.setTextColor(Color.parseColor("#000000"))
        alert.setTitle("Enter your user id")
        alert.setView(edittext)
        alert.setPositiveButton(R.string.yes)  { _: DialogInterface?, _: Int ->
            username = edittext.text.toString()
            credentials = Credentials(token)
            credentials?.username = username

            val dataPlatformType = DataPlatformType.HAT

            val dataPlatform = DataPlatformFactory.getDataPlatform(dataPlatformType, credentials)

            val hatFilesOps = (dataPlatform as HatPlatform).hatFilesOps
            val file =  getDatabasePath(PriVELTDatabase.PriVELTDatabaseName)

            val hatFileDetails = HatFileDetails(file.name, file.absolutePath, listOf("app-112-dev"))
            hatFileDetails.size = file.length()

            Executors.newSingleThreadExecutor().execute { hatFilesOps.upload(hatFileDetails) }
        }
        alert.show()

    }

    override fun onFailure(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun configureHat() {

        initNextButton()
        initHatLoginButton()
    }

    private fun initNextButton() {
        hatSwitch.visibility = View.INVISIBLE
        hatSwitch.setOnClickListener {
            // Create credentials based on the selected Data Platform:
            createDataPlatform()

            // Request File Upload:
            val file = getDatabasePath(PriVELTDatabase.PriVELTDatabaseName)
            val hatFilesDetails: HatFileDetails = createHatFileObject(file.absolutePath, file)!!
            UploadFileTask(AccountManager.getInstance().dataPlatformController, hatFilesDetails).execute()
        }
    }

    private fun createHatFileObject(filePath: String, file: File): HatFileDetails? {
        val hatFilesDetails = HatFileDetails(file.name, filePath, listOf(""))
        hatFilesDetails.size = file.length()
        return hatFilesDetails
    }

    private fun initHatLoginButton() {
        hatLoginButton.setOnClickListener {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(hatEmail.text.toString()).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // Reset objects:
            hatLogin = false
            credentials = Credentials()
            val alert = AlertDialog.Builder(this)
            alert.setTitle("HAT Signin/Signup")
            alert.setCancelable(true)
            alert.setNegativeButton("Close") { dialog: DialogInterface, id: Int -> dialog.dismiss() }
            val alertDialog = alert.create()
            val wv: WebView = createWebView()!!
            val email: String = hatEmail.text.toString()
            val hatAppName: String = "app-112-dev"
            val signupURL = HatClient.getSignupURL(email, hatAppName, getDeepLink())
            wv.loadUrl(signupURL)
            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    Log.i("WebView-Tag", "onPageFinished: $url")
                    if (parseHatLoginCallback(url)) {
                        alertDialog.dismiss()
                        hatLogin = true
                        setHatLoginResult()
                    }
                    Log.i("PriVELT-APP", credentials.toString())
                }
            }
            val wrapper = LinearLayout(this)

            val keyboardHack = EditText(this)

            keyboardHack.visibility = View.GONE
            wrapper.orientation = LinearLayout.VERTICAL;
            wrapper.addView(wv, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            alertDialog.setView(wrapper);
            alertDialog.show()
        }
    }

    private fun parseHatLoginCallback(url: String): Boolean {
        try {
            val aURL = URL(url)
            if (aURL.protocol.equals(DEEP_LINK_SCHEME, ignoreCase = true)) {
                val host = aURL.host
                if (host != null) {
                    if (host.contains("hubat.net")) {
                        val hatUsername = host.substring(0, host.indexOf('.'))
                        username = hatUsername
                        credentials?.username = hatUsername
                    } else if (host.equals(DEEP_LINK_DOMAIN, ignoreCase = true)) {
                        val query = aURL.query
                        if (query.startsWith("token=")) {
                            val appToken = query.replace("token=", "")
                            token = appToken
                            credentials?.token = appToken
                        }
                        return true
                    }
                }
            }
        } catch (e: MalformedURLException) {
            Log.e("PriVELT-APP", "onItemSelected", e)
        }
        return false
    }

    private fun createWebView(): WebView? {
        val wv = WebView(this)
        val webSettings = wv.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setGeolocationEnabled(true)
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.defaultTextEncodingName = "utf-8"
        wv.requestFocus(View.FOCUS_DOWN)
        return wv
    }

    private fun createDataPlatform() {
        credentials = null
        if (dataPlatformClient == null) {
            credentials = Credentials(username, "password")
            credentials?.token = token
            val hatClient = HatClient(credentials)
            dataPlatformClient = hatClient
        }

        // Add Hat App name:
        val appName = "app-112-dev"
        if (dataPlatformClient is HatClient) {
            (dataPlatformClient as HatClient).appName = appName
        }
        val dataPlatform =
                if (dataPlatformClient == null)
                    DataPlatformFactory.getDataPlatform(dataPlatformType, credentials)
                else
                    DataPlatformFactory.getDataPlatform(dataPlatformClient)
        AccountManager.getInstance().dataPlatform = dataPlatform
    }

    fun setHatAuthenticationResult(hatAuthenticationResponse: HatAuthenticationResponse?) {
        if (hatAuthenticationResponse == null) {
            hatSwitch.visibility = View.INVISIBLE
            return
        }
        token = hatAuthenticationResponse.accessToken
        hatSwitch.visibility = View.VISIBLE
    }

    fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun setHatLoginResult() {
        if (!hatLogin) {
            return
        }
        token = credentials!!.token
        hatSwitch.visibility = View.VISIBLE
    }
}