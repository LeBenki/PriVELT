/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.kent.university.privelt.BuildConfig
import com.kent.university.privelt.R
import com.kent.university.privelt.api.ServiceHelper
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.utils.EyePassword
import net.neferett.webviewsextractor.DataExtractor
import net.neferett.webviewsinjector.response.ResponseCallback
import net.neferett.webviewsinjector.response.ResponseEnum
import net.neferett.webviewsinjector.services.LoginService
import java.util.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var service: Service? = null

    @JvmField
    @BindView(R.id.best_poc)
    var button: Button? = null

    @JvmField
    @BindView(R.id.mail)
    var email: EditText? = null

    @JvmField
    @BindView(R.id.password)
    var password: EditText? = null

    @JvmField
    @BindView(R.id.progress_circular)
    var progressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.scripts)
    var recyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.eye_password)
    var eye: ImageView? = null

    @JvmField
    @BindView(R.id.remember_password)
    var rememberPassword: CheckBox? = null
    var loginService: LoginService? = null
    var alertDialog: AlertDialog? = null
    private var adapter: ScriptsAdapter? = null
    override fun getActivityLayout(): Int {
        return R.layout.activity_login
    }

    override fun configureViewModel() {}
    override fun configureDesign(savedInstanceState: Bundle?) {
        button!!.setOnClickListener(this)
        if (intent != null) {
            service = intent.getSerializableExtra(PARAM_SERVICE) as Service
        } else if (savedInstanceState != null) {
            service = savedInstanceState.getSerializable(PARAM_SERVICE) as Service?
        }
        assert(service != null)
        title = service!!.name
        EyePassword.configureEye(eye, password)
        val serviceHelper = ServiceHelper(this)
        loginService = serviceHelper.getServiceWithName(service!!.name)
        rememberPassword!!.isChecked = service!!.isPasswordSaved
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        val dataExtractor = DataExtractor(loginService)
        dataExtractor.serviceName
        adapter = ScriptsAdapter(dataExtractor.stringScripts, Arrays.asList(*service!!.unConcatenatedScripts))
        recyclerView!!.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(PARAM_SERVICE, service)
    }

    fun processLogin() {
        progressBar!!.visibility = View.VISIBLE
        button!!.isEnabled = false
        if (BuildConfig.DEBUG) showAlertDebug()
        loginService!!.autoLogin(email!!.text.toString(), password!!.text.toString(), object : ResponseCallback() {
            override fun getResponse(responseEnum: ResponseEnum, data: String) {
                if (responseEnum != ResponseEnum.SUCCESS) {
                    button!!.isEnabled = true
                    showAlertDebug()
                    Toast.makeText(this@LoginActivity, responseEnum.getName(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_LONG).show()
                }
                progressBar!!.visibility = View.GONE
            }
        })
    }

    override fun onClick(view: View) {
        if (email!!.text.toString().isEmpty() || password!!.text.toString().isEmpty()) {
            Toast.makeText(this@LoginActivity, "", Toast.LENGTH_LONG).show()
            return
        }
        processLogin()
    }

    private fun showAlertDebug() {
        if (loginService!!.webview.parent != null) {
            (loginService!!.webview.parent as ViewGroup).removeView(loginService!!.webview)
        }
        if (alertDialog == null || !alertDialog!!.isShowing) {
            val dialogBuilder = AlertDialog.Builder(this@LoginActivity)
            dialogBuilder.setView(loginService!!.webview)
            alertDialog = dialogBuilder.create()
            alertDialog?.setCanceledOnTouchOutside(true)
            alertDialog?.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.check_menu, menu)
        return true
    }

    val isValidInput: Boolean
        get() {
            if (email!!.text.toString().isEmpty() || password!!.text.toString().isEmpty()) {
                Toast.makeText(this@LoginActivity, R.string.email_or_password_empty, Toast.LENGTH_LONG).show()
                return false
            }
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.check) {
            if (isValidInput) {
                val intent = Intent()
                intent.putExtra(PARAM_USER, email!!.text.toString())
                intent.putExtra(PARAM_PASSWORD, password!!.text.toString())
                service!!.isPasswordSaved = rememberPassword!!.isChecked
                service!!.concatenatedScripts = adapter!!.concatenatedScriptsChecked
                intent.putExtra(PARAM_SERVICE, service)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PARAM_USER = "PARAM_USER"
        const val PARAM_PASSWORD = "PARAM_PASSWORD"
        const val PARAM_SERVICE = "PARAM_SERVICE"
    }
}