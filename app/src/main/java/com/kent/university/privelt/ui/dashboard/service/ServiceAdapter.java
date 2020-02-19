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

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_SERVICE = 1;
    private final static int TYPE_DATA = 2;

    private List<Service> services;
    private LinkedHashMap<Service, List<UserData>> linkedCredentials;
    private List<String> watchList;

    private LinkedHashMap<String, LinkedHashMap<Service, List<UserData>>> linkedTypes;

    ServiceAdapter() {
        this.services = new ArrayList<>();
        this.linkedCredentials = new LinkedHashMap<>();
        this.linkedTypes = new LinkedHashMap<>();
        this.watchList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < linkedCredentials.size())
            return TYPE_SERVICE;
        else
            return TYPE_DATA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_service, parent, false);
        return viewType == TYPE_SERVICE ? new ServiceViewHolder(view) : new DataCentricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int type = getItemViewType(position);
        if (type == TYPE_SERVICE) {
            ServiceViewHolder serviceViewHolder = (ServiceViewHolder) holder;
            serviceViewHolder.bind(services.get(position), linkedCredentials.get(services.get(position)), watchList.contains(services.get(position).getName()));
        }
        else {
            try {
                DataCentricViewHolder dataCentricViewHolder = (DataCentricViewHolder) holder;
                String typeData = new ArrayList<>(linkedTypes.keySet()).get(position - linkedCredentials.size());
                dataCentricViewHolder.bind(typeData, linkedTypes.get(typeData), watchList.contains(typeData));
            }
            catch (Exception e){}
        }
    }

    void updateServices(List<Service> services) {
        this.services = services;
    }

    @Override
    public int getItemCount() {
        return linkedCredentials.size() + linkedTypes.size();
    }

    private Service getServiceFromIndex(long id) {
        for (Service service : services) {
            if (service.getId() == id)
                return service;
        }
        return null;
    }

    void updateUserDatas(List<UserData> userDataList, boolean[] filters, List<String> watchList) {
        if (userDataList == null)
            return;
        this.watchList = watchList;
        linkedCredentials.clear();
        linkedTypes.clear();

        for (UserData userData : userDataList) {

            if ((filters == null || filters[1]) && (filters == null || (!filters[2] || watchList.contains(getServiceFromIndex(userData.getServiceId()).getName())))) {
                if (!linkedCredentials.containsKey(getServiceFromIndex(userData.getServiceId()))) {
                    linkedCredentials.put(getServiceFromIndex(userData.getServiceId()), new LinkedList<>(Collections.singletonList(userData)));
                } else {
                    List<UserData> tmp = linkedCredentials.get(getServiceFromIndex(userData.getServiceId()));
                    assert tmp != null;
                    tmp.add(userData);
                    linkedCredentials.put(getServiceFromIndex(userData.getServiceId()), tmp);
                }
            }

            if ((filters == null || filters[0]) && (filters == null || (!filters[2] || watchList.contains(userData.getType())))) {
                if (!linkedTypes.containsKey(userData.getType())) {
                    LinkedHashMap<Service, List<UserData>> map = new LinkedHashMap<>();
                    map.put(getServiceFromIndex(userData.getServiceId()), Collections.singletonList(userData));
                    linkedTypes.put(userData.getType(), map);
                } else {
                    LinkedHashMap<Service, List<UserData>> tmp = linkedTypes.get(userData.getType());
                    if (!tmp.containsKey(getServiceFromIndex(userData.getServiceId()))) {
                        tmp.put(getServiceFromIndex(userData.getServiceId()), new LinkedList<>(Collections.singletonList(userData)));
                    } else {
                        List<UserData> data = new ArrayList<>(tmp.get(getServiceFromIndex(userData.getServiceId())));
                        data.add(userData);
                        tmp.put(getServiceFromIndex(userData.getServiceId()), data);
                    }
                    linkedTypes.put(userData.getType(), tmp);
                }
            }
        }
    }
}
