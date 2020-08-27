/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.base

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.kent.university.privelt.R
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.utils.DriveServiceHelper
import com.kent.university.privelt.utils.hat.AccountManager
import com.kent.university.privelt.utils.hat.DownloadFileTask
import com.kent.university.privelt.utils.hat.UploadFileTask
import com.privelt.pda.dataplatform.DataPlatformFactory
import com.privelt.pda.dataplatform.DataPlatformType
import com.privelt.pda.dataplatform.generic.Credentials
import com.privelt.pda.dataplatform.generic.DataPlatformClient
import com.privelt.pda.dataplatform.hat.HatClient
import com.privelt.pda.dataplatform.hat.files.HatFileDetails
import com.privelt.pda.dataplatform.hat.response.HatAuthenticationResponse
import java.io.File
import java.net.MalformedURLException
import java.net.URL

abstract class PDAActivity : BaseActivity() {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    protected var listener: GoogleDriveListener? = null
    private var fileId = ""

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

    protected fun googleDriveConnection() {
        googleDriveConnectionAndDownload(false, "")
    }

    abstract fun hatLogged()

    protected fun googleDriveConnectionAndDownload(download: Boolean, fileId: String) {
        this.fileId = fileId
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            requestSignIn()
        } else {
            val credential = GoogleAccountCredential.usingOAuth2(
                    this, listOf(DriveScopes.DRIVE_FILE))
            credential.selectedAccount = account.account
            val googleDriveService = Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            GsonFactory(),
                            credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build()
            mDriveServiceHelper = DriveServiceHelper(googleDriveService)
            if (download) downloadFile()
        }
    }

    private fun requestSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun downloadFile() {
        mDriveServiceHelper!!.downloadFile(fileId, getDatabasePath(PriVELTDatabase.PriVELTDatabaseName).path)
                .addOnSuccessListener { listener!!.onDownloadSuccess() }
                .addOnFailureListener { listener!!.onDownloadFailure() }
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                    val credential = GoogleAccountCredential.usingOAuth2(
                            this, listOf(DriveScopes.DRIVE_FILE))
                    credential.selectedAccount = googleAccount.account
                    val googleDriveService = Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    GsonFactory(),
                                    credential)
                            .setApplicationName("Drive API Migration")
                            .build()
                    mDriveServiceHelper = DriveServiceHelper(googleDriveService)
                    if (listener != null) downloadFile()
                }.addOnSuccessListener { listener!!.onConnectionSuccess() }
                .addOnFailureListener { exception: Exception? -> Log.e("TAG", "Unable to sign in.", exception) }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 1
    }

    //HAT

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
            //hatSwitch.visibility = View.INVISIBLE
            return
        }
        token = hatAuthenticationResponse.accessToken
        //hatSwitch.visibility = View.VISIBLE
    }

    fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun setHatLoginResult() {
        if (!hatLogin) {
            return
        }
        token = credentials!!.token
        //hatSwitch.visibility = View.VISIBLE
    }

    private fun createHatFileObject(filePath: String, file: File): HatFileDetails? {
        val hatFilesDetails = HatFileDetails(file.name, filePath, listOf(""))
        hatFilesDetails.size = file.length()
        return hatFilesDetails
    }

    fun uploadDatabaseWithHAT() {
        createDataPlatform()

        // Request File Upload:
        val file = getDatabasePath(PriVELTDatabase.PriVELTDatabaseName)
        val hatFilesDetails: HatFileDetails = createHatFileObject(file.absolutePath, file)!!
        UploadFileTask(AccountManager.getInstance().dataPlatformController, hatFilesDetails).execute()
    }

    fun downloadFileWithHAT(fileId: String) {
        createDataPlatform()
        DownloadFileTask(AccountManager.getInstance().dataPlatformController, fileId).execute()
    }

    fun loginHAT(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show()
            return
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
        val hatAppName = "app-112-dev"
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
                    hatLogged()
                }
                Log.i("PriVELT-APP", credentials.toString())
            }
        }
        val wrapper = LinearLayout(this)

        val keyboardHack = EditText(this)

        keyboardHack.visibility = View.GONE
        wrapper.orientation = LinearLayout.VERTICAL
        wrapper.addView(wv, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        alertDialog.setView(wrapper)
        alertDialog.show()
    }
}