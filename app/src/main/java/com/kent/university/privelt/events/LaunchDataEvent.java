package com.kent.university.privelt.events;

import com.kent.university.privelt.model.Service;

public class LaunchDataEvent {

    public Service service;

    public LaunchDataEvent(Service service) {
        this.service = service;
    }
}
