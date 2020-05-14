/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.ui.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kent.university.privelt.R
import com.kent.university.privelt.base.GoogleDriveActivity
import com.kent.university.privelt.service.ProcessMainClass
import com.kent.university.privelt.service.restarter.RestartServiceBroadcastReceiver.Companion.scheduleJob
import com.kent.university.privelt.ui.dashboard.card.CardFragment
import com.kent.university.privelt.ui.dashboard.sensors.SensorFragment
import com.kent.university.privelt.ui.dashboard.user.UserFragment
import com.kent.university.privelt.ui.settings.SettingsActivity
import com.kent.university.privelt.utils.sensors.TemporarySavePermissions
import com.kent.university.privelt.utils.sensors.TemporarySaveSensors
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : GoogleDriveActivity() {
    private var toolbar: ActionBar? = null

    override fun configureViewModel() {}

    override fun configureDesign(savedInstanceState: Bundle?) {
        toolbar = supportActionBar
        navigation_view!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        toolbar!!.setTitle(R.string.services)
        loadFragment(CardFragment())
        launchService()
        TemporarySaveSensors.load(applicationContext)
        TemporarySavePermissions.load(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(R.string.data_extraction).setMessage(R.string.overlay_permission).setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    this.startActivity(myIntent)
                }.setNegativeButton(R.string.no, null)
                alertDialog.show()
            }
        }
    }

    private fun launchService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(applicationContext)
        } else {
            val bck = ProcessMainClass()
            bck.launchService(applicationContext)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragment: Fragment
        when (item.itemId) {
            R.id.navigation_service -> {
                toolbar!!.setTitle(R.string.services)
                fragment = CardFragment()
                loadFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sensors -> {
                toolbar!!.setTitle(R.string.permissions)
                fragment = SensorFragment()
                loadFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_user -> {
                toolbar!!.setTitle(R.string.user)
                fragment = UserFragment()
                loadFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val settings = menu.findItem(R.id.settings)
        val filter = menu.findItem(R.id.filter)
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is UserFragment) {
            settings.isVisible = true
            filter.isVisible = false
        } else {
            settings.isVisible = false
            filter.isVisible = fragment is CardFragment
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            startActivityForResult(Intent(this, SettingsActivity::class.java), REQUEST_SETTINGS)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_SETTINGS && resultCode == Activity.RESULT_OK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    override val activityLayout: Int
        get() = R.layout.activity_dashboard

    override fun onBackPressed() {}

    companion object {
        private const val REQUEST_SETTINGS = 7654
    }
}