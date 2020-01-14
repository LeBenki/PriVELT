package com.kent.university.privelt.events;

import com.kent.university.privelt.model.Service;

public class UpdateCredentialsEvent {

    public Service service;

    public UpdateCredentialsEvent(Service service) {
        this.service = service;
    }
}
