/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.privacy

import android.os.Bundle
import android.webkit.WebViewClient
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyActivity: BaseActivity() {

    var link : String? = null

    companion object {
        const val LINK_PARAM = "link"
    }

    override val activityLayout: Int
        get() = R.layout.activity_privacy

    override fun configureViewModel() {
    }

    override fun configureDesign(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {
            link = savedInstanceState.getString(LINK_PARAM)
        } else if (intent != null) {
            link = intent.getStringExtra(LINK_PARAM)
        }

        gdprWebView.settings.javaScriptEnabled = true
        gdprWebView.webViewClient = WebViewClient()
        gdprWebView.loadUrl(link)
    }
}