package com.kent.university.privelt.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.PriVELTApplication;
import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.di.DaggerPriVELTComponent;
import com.kent.university.privelt.di.PriVELTComponent;
import com.kent.university.privelt.di.RoomModule;
import com.kent.university.privelt.viewmodel.PriVELTViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected abstract int getFragmentLayout();
    protected abstract void configureViewModel();
    protected abstract void configureDesign();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(this.getFragmentLayout(), container, false);

        // Using the ButterKnife library
        ButterKnife.bind(this, view);

        // ViewModel
        this.configureViewModel();

        configureDesign();

        return view;
    }

    protected <T extends ViewModel> T getViewModel(final Class<T> className) {
        // Component
        final PriVELTComponent component = DaggerPriVELTComponent.builder().roomModule(new RoomModule(getContext())).build();

        // ViewModelFactory
        final PriVELTViewModelFactory factory = component.getViewModelFactory();

        return new ViewModelProvider(this, factory).get(className);
    }

    protected PasswordManager getIdentityManager() {
        return ((PriVELTApplication) getContext().getApplicationContext()).getIdentityManager();
    }

    protected ServiceHelper getServiceHelper() {
        return ((PriVELTApplication) getContext().getApplicationContext()).getServiceHelper();
    }
}
