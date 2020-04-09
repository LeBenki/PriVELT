/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils

import android.content.SharedPreferences
import android.text.TextUtils
import java.util.*

class WatchListHelper(private val sharedPreferences: SharedPreferences) {
    private val watchList: MutableList<String?>
    fun getWatchList(): List<String?> {
        return watchList
    }

    fun changeWatchListStatus(cardTitle: String?) {
        if (watchList.contains(cardTitle)) watchList.remove(cardTitle) else watchList.add(cardTitle)
        saveWatchList()
    }

    private fun saveWatchList() {
        sharedPreferences.edit().putString(PARAM_WATCH_LIST, TextUtils.join(",", watchList)).apply()
    }

    companion object {
        private const val PARAM_WATCH_LIST = "PARAM_WATCH_LIST"
    }

    init {
        watchList = ArrayList(Arrays.asList(*sharedPreferences.getString(PARAM_WATCH_LIST, "")!!.split(",").toTypedArray()))
        if (watchList.contains("")) watchList.remove("")
    }
}