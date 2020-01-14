package com.kent.university.privelt.ui.login;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScriptsAdapter extends RecyclerView.Adapter<ScriptViewHolder> {

    private LinkedHashMap<String, Boolean> scripts;

    public ScriptsAdapter(List<String> scripts, List<String> alreadyChecked) {

        this.scripts = new LinkedHashMap<>();

        for (String script : scripts) {
            this.scripts.put(script, false);
        }
        for (String script : alreadyChecked) {
            Log.d("TAG", script);
            if (!script.isEmpty())
                this.scripts.put(script, true);
        }
    }

    @NonNull
    @Override
    public ScriptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_script, parent, false);
        return new ScriptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScriptViewHolder holder, int position) {
        holder.bind((String) scripts.keySet().toArray()[position], scripts);
    }

    @Override
    public int getItemCount() {
        return scripts.size();
    }

    public String getConcatenatedScriptsChecked() {
        String result = "";
        for (Map.Entry<String, Boolean> entry : scripts.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if (value)
                result = result.concat(key.concat(Service.DELIMITER));
        }
        return result;
    }
}
