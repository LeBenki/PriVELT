package com.kent.university.privelt.base;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.api.PasswordManager;
import com.kent.university.privelt.api.ServiceHelper;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    protected PasswordManager getIdentityManager() {
        return ((PriVELT) getContext().getApplicationContext()).getIdentityManager();
    }

    protected ServiceHelper getServiceHelper() {
        return ((PriVELT) getContext().getApplicationContext()).getServiceHelper();
    }
}
