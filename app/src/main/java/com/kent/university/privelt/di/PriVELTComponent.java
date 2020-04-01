package com.kent.university.privelt.di;

import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RoomModule.class})
public interface PriVELTComponent {
    PriVELTViewModelFactory getViewModelFactory();
}
