package com.kent.university.privelt.base;

import com.kent.university.privelt.PriVELT;
import com.kent.university.privelt.api.IdentityManager;
import com.kent.university.privelt.api.ServiceHelper;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {

    protected IdentityManager getIdentityManager() {
        return ((PriVELT) getContext().getApplicationContext()).getIdentityManager();
    }

    protected ServiceHelper getServiceHelper() {
        return ((PriVELT) getContext().getApplicationContext()).getServiceHelper();
    }
}
