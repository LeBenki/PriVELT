/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.api;

import android.content.Context;

import com.kent.university.privelt.model.Service;
import com.kent.university.webviewautologin.services.LoginService;
import com.kent.university.webviewautologin.services.ServiceManager;

import java.util.ArrayList;

public class ServiceHelper {

    private ServiceManager serviceManager;
    private ArrayList<String> serviceNames;
    private ArrayList<Integer> serviceResIds;

    public ServiceHelper(Context context) {
        this.serviceManager = new ServiceManager(context);
        this.serviceManager.loadServices();

        serviceNames = new ArrayList<>();
        serviceResIds = new ArrayList<>();

        for (LoginService loginService : serviceManager.getServiceList()) {
            serviceNames.add(loginService.getName());
            serviceResIds.add(loginService.getDrawableLogo());
        }
    }

    public ArrayList<String> getRemainingServices(ArrayList<Service> subscribedServices) {
        ArrayList<String> notSubscribed = new ArrayList<>(this.getServiceNames());

        for (Service service : subscribedServices)
            notSubscribed.remove(service.getName());

        return notSubscribed;
    }

    private ArrayList<String> getServiceNames() {
        return serviceNames;
    }

    public int getResIdWithName(String name) {
        for (int i = 0; i < serviceNames.size(); i++){
            if (serviceNames.get(i).equals(name))
                return serviceResIds.get(i);
        }
        return -1;
    }

    public LoginService getServiceWithName(String name) {

        for (int i = 0; i < serviceNames.size(); i++){
            if (serviceNames.get(i).equals(name)) {
                return serviceManager.getServiceList().get(i);
            }
        }
        return null;
    }
}
