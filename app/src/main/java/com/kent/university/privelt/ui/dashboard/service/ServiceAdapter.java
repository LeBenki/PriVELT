package com.kent.university.privelt.ui.dashboard.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceViewHolder> {

    private List<Service> services;
    private LinkedHashMap<Service, List<UserData>> linkedCredentials;

    ServiceAdapter() {
        this.services = new ArrayList<>();
        this.linkedCredentials = new LinkedHashMap<>();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.bind(services.get(position), linkedCredentials.get(services.get(position)));
    }

    void updateServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    private Service getServiceFromIndex(long id) {
        for (Service service : services) {
            if (service.getId() == id)
                return service;
        }
        return null;
    }

    void updateUserDatas(List<UserData> userDataList) {
        if (userDataList == null)
            return;
        linkedCredentials.clear();

        for (UserData userData : userDataList) {
            if (!linkedCredentials.containsKey(getServiceFromIndex(userData.getServiceId()))) {
                linkedCredentials.put(getServiceFromIndex(userData.getServiceId()), new LinkedList<>(Collections.singletonList(userData)));
            }
            else {
                List<UserData> tmp = linkedCredentials.get(getServiceFromIndex(userData.getServiceId()));
                assert tmp != null;
                tmp.add(userData);
                linkedCredentials.put(getServiceFromIndex(userData.getServiceId()), tmp);
            }
        }
    }
}
