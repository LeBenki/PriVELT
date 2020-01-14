package com.kent.university.privelt.ui.login;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kent.university.privelt.R;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ScriptViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.check_script)
    CheckBox script;

    ScriptViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String name, LinkedHashMap<String, Boolean> scripts) {
        script.setText(name);
        script.setChecked(scripts.get(name));
        script.setOnCheckedChangeListener((compoundButton, b) -> scripts.put(name, b));
    }
}
