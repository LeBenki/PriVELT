/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ProcessMainClass {
    private static final String TAG = ProcessMainClass.class.getSimpleName();
    private static Intent serviceIntent = null;

    public ProcessMainClass() {
    }


    private void setServiceIntent(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, Service.class);
        }
    }
    /**
     * launching the service
     */
    public void launchService(Context context) {
        if (context == null) {
            return;
        }
        setServiceIntent(context);
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}

