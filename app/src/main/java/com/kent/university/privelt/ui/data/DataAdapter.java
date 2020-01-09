package com.kent.university.privelt.ui.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.UserData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataViewHolder> {

    private List<UserData> userData;

    DataAdapter(ArrayList<UserData> userData) {
        this.userData = userData;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_userdata, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.bind(userData.get(position));
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    void setUserData(List<UserData> userData) {
        this.userData = userData;
    }
}
