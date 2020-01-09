package com.kent.university.privelt.events;

public class LaunchDataEvent {

    public String service;
    public long serviceId;

    public LaunchDataEvent(String service, long serviceId) {
        this.service = service;
        this.serviceId = serviceId;
    }
}
