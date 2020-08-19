/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.api

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.kent.university.privelt.BuildConfig
import com.kent.university.privelt.PriVELTApplication
import com.kent.university.privelt.R
import com.kent.university.privelt.database.PriVELTDatabase
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.utils.DriveServiceHelper
import net.neferett.webviewsextractor.DataExtractor
import net.neferett.webviewsextractor.extraction.Status
import net.neferett.webviewsinjector.response.ResponseCallback
import net.neferett.webviewsinjector.response.ResponseEnum
import org.json.JSONArray
import java.util.*

object DataExtraction {
    private val TAG = DataExtractor::class.simpleName
    private fun saveToGoogleDrive(applicationContext: Context) {
        val serviceDataRepository = PriVELTDatabase.getInstance(applicationContext)?.serviceDao()
        val settingsDataRepository = PriVELTDatabase.getInstance(applicationContext)?.settingsDao()
        val settings = settingsDataRepository?.instantSettings
        if (settings != null && settings.isGoogleDriveAutoSave) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(PriVELTApplication.instance)
                val credential = GoogleAccountCredential.usingOAuth2(
                        PriVELTApplication.instance, listOf(DriveScopes.DRIVE_FILE))
                credential.selectedAccount = account!!.account
                val googleDriveService = Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                GsonFactory(),
                                credential)
                        .setApplicationName(PriVELTApplication.instance?.resources?.getString(R.string.app_name))
                        .build()
                val mDriveServiceHelper = DriveServiceHelper(googleDriveService)
                val serviceList = serviceDataRepository?.allServices
                val oldServices = cloneList(serviceList!!)
                for (i in serviceList.indices) {
                    serviceList[i].password = ""
                    serviceList[i].user = ""
                    serviceList[i].isPasswordSaved = false
                    serviceDataRepository.updateServices(serviceList[i])
                }
                val s = mDriveServiceHelper.uploadFile(PriVELTApplication.instance?.getDatabasePath(PriVELTDatabase.PriVELTDatabaseName)!!, settings.googleDriveFileID)
                for (service in oldServices) serviceDataRepository.updateServices(service)
                settings.googleDriveFileID = s
                settingsDataRepository.updateSettings(settings)
                if (BuildConfig.DEBUG) Log.d(TAG, "Google drive saved")
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Google drive not saved " + e.message)
            }
        }
    }

    fun processExtractionForEachService(applicationContext: Context) {
        val serviceHelper = ServiceHelper((applicationContext as PriVELTApplication).currentActivity)
        val serviceDataRepository = PriVELTDatabase.getInstance(applicationContext)?.serviceDao()
        val services = serviceDataRepository?.allServices
        for (service in services!!) {
            if (service.isPasswordSaved)
                processDataExtraction(serviceHelper, service, service.user, service.password, applicationContext)
        }
    }

    private fun cloneList(list: List<Service>): List<Service> {
        val clone: MutableList<Service> = ArrayList(list.size)
        for (item in list) clone.add(item.clone() as Service)
        return clone
    }

    fun processDataExtraction(serviceHelper: ServiceHelper, service: Service, email: String?, password: String?, applicationContext: Context) {
        val time = System.currentTimeMillis()
        val userDataRepository = PriVELTDatabase.getInstance(applicationContext)?.userDataDao()
        val previousUserData = userDataRepository?.getUserDataForAService(service.id)
        val loginService = serviceHelper.getServiceWithName(service.name)
        val dataExtractor = DataExtractor(loginService)
        val allUserData = ArrayList<UserData>()
        (applicationContext as PriVELTApplication).currentActivity?.runOnUiThread {
            loginService?.autoLogin(email, password, object : ResponseCallback() {
                override fun getResponse(responseEnum: ResponseEnum, data: String) {
                    if (BuildConfig.DEBUG) Log.d(TAG + " FOR SERVICE=" + service.name, responseEnum.toString())
                    if (responseEnum == ResponseEnum.SUCCESS) {
                        dataExtractor.injectAll(applicationContext.currentActivity) { jsonArray: JSONArray?, status: Status ->
                            if (BuildConfig.DEBUG) Log.d(TAG + " FOR SERVICE=" + service.name, status.toString())
                            if (jsonArray != null) {
                                allUserData.addAll(parseJSON(jsonArray, service, time))
                                if (BuildConfig.DEBUG) Log.d(TAG + " FOR SERVICE=" + service.name, jsonArray.toString())
                            }
                            if (status.isDone) {
                                if (BuildConfig.DEBUG) Log.d(TAG + " FOR SERVICE=" + service.name, "LOGIN SERVICE:" + allUserData.size)
                                for (userData in allUserData) {
                                    //check if we have already extracted this data
                                    val res = previousUserData?.find {
                                        it.value == userData.value && it.title == userData.title
                                    }
                                    if (res == null)
                                        userDataRepository?.insertUserData(userData)
                                }
                                saveToGoogleDrive(applicationContext)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun parseJSON(jsonArray: JSONArray, service: Service, time: Long): ArrayList<UserData> {
        val array = ArrayList<UserData>()
        try {
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val data = obj.getJSONArray("data")
                val td: MutableList<String?> = ArrayList()
                for (j in 0 until data.length()) {
                    td.add(data.getString(j))
                }
                val userData = UserData(
                        obj.getString("title"),
                        obj.getString("type"),
                        obj.getString("value"),
                        TextUtils.join(UserData.DELIMITER, td),
                        service.id,
                        time)
                array.add(userData)
            }
        } catch (ignored: Exception) {
        }
        return array
    }
}