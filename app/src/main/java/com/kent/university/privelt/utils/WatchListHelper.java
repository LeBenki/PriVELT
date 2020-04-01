/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchListHelper {

    private SharedPreferences sharedPreferences;
    private List<String> watchList;
    private final static String PARAM_WATCH_LIST = "PARAM_WATCH_LIST";

    public WatchListHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        watchList = new ArrayList<>(Arrays.asList(sharedPreferences.getString(PARAM_WATCH_LIST, "").split(",")));
        if (watchList.contains(""))
            watchList.remove("");
    }

    public List<String> getWatchList() {
        return watchList;
    }

    public void changeWatchListStatus(String cardTitle) {
        if (watchList.contains(cardTitle))
            watchList.remove(cardTitle);
        else
            watchList.add(cardTitle);
        saveWatchList();
    }

    private void saveWatchList() {
        sharedPreferences.edit().putString(PARAM_WATCH_LIST, TextUtils.join(",", watchList)).apply();
    }
}
