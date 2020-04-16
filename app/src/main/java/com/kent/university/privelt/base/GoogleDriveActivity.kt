/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.base

import android.app.Activity
import android.content.Intent
import android.util.Log
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

abstract class GoogleDriveActivity : BaseActivity() {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    protected var listener: GoogleDriveListener? = null
    private var fileId = ""
    protected fun googleDriveConnection() {
        googleDriveConnectionAndDownload(false, "")
    }

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
}