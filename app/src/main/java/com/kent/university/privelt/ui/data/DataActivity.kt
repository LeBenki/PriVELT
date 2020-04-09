/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.data

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import com.kent.university.privelt.model.Service
import com.kent.university.privelt.model.UserData
import com.kent.university.privelt.ui.login.LoginActivity
import java.util.*

class DataActivity : BaseActivity() {
    private var service: String? = null

    private var type: String? = null

    @JvmField
    @BindView(R.id.recycler_view_userdata)
    var recyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.progress_layout)
    var progressLayout: LinearLayout? = null

    private var dataViewModel: DataViewModel? = null

    private var dataAdapter: DataAdapter? = null

    override fun getActivityLayout(): Int {
        return R.layout.activity_data
    }

    override fun configureDesign(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            service = savedInstanceState.getString(LoginActivity.PARAM_SERVICE)
            type = savedInstanceState.getString(PARAM_TYPE)
        } else if (intent != null) {
            service = intent.getStringExtra(LoginActivity.PARAM_SERVICE)
            type = intent.getStringExtra(PARAM_TYPE)
        }
        configureRecyclerView()
        getServices()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(LoginActivity.PARAM_SERVICE, service)
        outState.putSerializable(PARAM_TYPE, type)
    }

    private fun configureRecyclerView() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        dataAdapter = DataAdapter(ArrayList())
        recyclerView!!.adapter = dataAdapter
    }

    private fun getServices() {
        dataViewModel!!.services.observe(this, Observer { services: List<Service> -> updateServices(services) })
    }

    private fun getUserDatas(id: Long, type: String?) {
        dataViewModel!!.getUserData(id, type).observe(this, Observer { userData: List<UserData> -> updateUserData(userData) })
    }

    private fun updateServices(services: List<Service>) {
        var id: Long = -1
        for (service in services) if (service.name == this.service) id = service.id
        getUserDatas(id, type)
    }

    private fun updateUserData(userData: List<UserData>) {
        if (userData.isEmpty()) {
            progressLayout!!.visibility = View.VISIBLE
        } else {
            dataAdapter!!.setUserData(userData)
            dataAdapter!!.notifyDataSetChanged()
            progressLayout!!.visibility = View.GONE
        }
    }

    override fun configureViewModel() {
        dataViewModel = getViewModel(DataViewModel::class.java)
        dataViewModel?.init()
    }

    companion object {
        const val PARAM_TYPE = "PARAM_TYPE"
    }
}