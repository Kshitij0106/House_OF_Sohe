package com.house_of_sohe.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.R;

public class TopHeadingsViewHolder extends RecyclerView.ViewHolder {

    public TextView title,seeAll;
    public RecyclerView productsRecyclerView;

    public TopHeadingsViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        seeAll = itemView.findViewById(R.id.seeAllButton);
        productsRecyclerView = itemView.findViewById(R.id.productsRecyclerView);
    }
}
