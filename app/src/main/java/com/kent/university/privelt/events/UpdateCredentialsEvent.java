package com.kent.university.privelt.events;

public class UpdateCredentialsEvent {

    public String service;

    public UpdateCredentialsEvent(String service) {
        this.service = service;
    }
}
