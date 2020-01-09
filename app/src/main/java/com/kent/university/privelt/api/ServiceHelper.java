package com.kent.university.privelt.api;

import android.content.Context;

import com.kent.university.privelt.model.Service;
import com.kent.university.webviewautologin.services.LoginService;
import com.kent.university.webviewautologin.services.ServiceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public ArrayList<String> getRemainingServices(ArrayList<Service> subscribedServices) {
        ArrayList<String> notSubscribed = (ArrayList<String>) this.getServiceNames().clone();

        for (Service service : subscribedServices)
            notSubscribed.remove(service.getName());

        return notSubscribed;
    }

    public ArrayList<String> getServiceNames() {
        return serviceNames;
    }

    public ArrayList<Integer> getServiceResIds() {
        return serviceResIds;
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
            if (serviceNames.get(i).equals(name))
                return serviceManager.getServiceList().get(i);
        }
        return null;
    }
}
