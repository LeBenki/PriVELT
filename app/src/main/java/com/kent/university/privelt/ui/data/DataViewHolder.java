package com.kent.university.privelt.ui.data;

import android.view.View;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.model.UserData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DataViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.value)
    TextView value;

    @BindView(R.id.concatenated_data)
    TextView concatenatedData;

    DataViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bind(UserData userData) {
        title.setText(userData.getTitle());
        value.setText(userData.getValue());
        concatenatedData.setText(userData.getConcatenatedData());
    }
}
