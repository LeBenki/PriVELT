/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class WebViewDialog internal constructor(private val email: String) : DialogFragment() {

    private lateinit var listener: AuthenticationListener

    interface AuthenticationListener {
        fun onSuccess(token: String)
        fun onFailure(error: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as AuthenticationListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement AuthenticationListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)

        val wv = WebView(context)
        wv.loadUrl("https://hatters.dataswift.io/services/daas/signup?email=$email&application_id=app-112-dev&redirect_uri=https://hatters.dataswift.io/hat/signin")
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //view.loadUrl(url)
                when {
                    url.contains("token=") -> listener.onSuccess(url.split("token=")[1])
                    url.contains("error_reason") -> listener.onFailure(url.split("error_reason=")[1])
                    url.contains("login") -> listener.onFailure("An account already exist with this email")
                }
                dismiss()
                return true
            }
        }

        builder.setView(wv)
        return builder.create()
    }
}