/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.gdpr

import android.os.Bundle
import android.webkit.WebViewClient
import com.kent.university.privelt.R
import com.kent.university.privelt.base.BaseActivity
import kotlinx.android.synthetic.main.activity_gdpr.*

class GDPRActivity: BaseActivity() {
    override val activityLayout: Int
        get() = R.layout.activity_gdpr

    override fun configureViewModel() {
    }

    override fun configureDesign(savedInstanceState: Bundle?) {
        gdprWebView.settings.javaScriptEnabled = true
        gdprWebView.webViewClient = WebViewClient()
        gdprWebView.loadUrl("https://www.facebook.com/policy.php")
    }
}