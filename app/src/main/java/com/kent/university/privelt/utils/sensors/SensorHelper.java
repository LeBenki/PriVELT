/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sensors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.kent.university.privelt.model.Application;
import com.kent.university.privelt.model.Sensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorHelper {

    private static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private static String parseName(String name) {
        if (name == null || name.isEmpty())
            return name;
        String appName = name.substring(name.lastIndexOf(".") + 1).replaceAll("Application|App", "");
        if (appName.isEmpty())
            return name;
        else
            return appName;
    }

    private static boolean checkIfApplicationHasPermission(Application application, String sensor) {
        for (String permission : application.getPermissions()) {
            if (permission.toLowerCase().contains(sensor.toLowerCase()))
                return true;
        }
        return false;
    }

    public static List<Sensor> getSensorsInformation(Context context) {
        List<Application> applications = getApplicationsInformation(context);
        List<Sensor> sensors = Arrays.asList(Sensor.values());

        for (Sensor sensor : sensors) {
            sensor.getApplications().clear();
            for (Application application : applications) {
                if (checkIfApplicationHasPermission(application, sensor.getName())) {
                    sensor.addApplication(application);
                }
            }
        }
        return sensors;
    }

    private static List<Application> getApplicationsInformation(Context context) {
        List<Application> applications = new ArrayList<>();
        StringBuilder appNameAndPermissions = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            if (isSystemPackage(applicationInfo))
                continue;
            String name = parseName(applicationInfo.name);
            if (name == null || name.isEmpty())
                continue;
            Application application = new Application(name);
            Log.d("test", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                appNameAndPermissions.append(packageInfo.packageName).append("*******:\n");

                //Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (String requestedPermission : requestedPermissions) {
                        Log.d("test", requestedPermission);
                        appNameAndPermissions.append(requestedPermission).append("\n");
                        application.addPermission(requestedPermission);
                    }
                }
                appNameAndPermissions.append("\n");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            applications.add(application);
        }
        return applications;
    }
}
