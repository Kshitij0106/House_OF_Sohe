package com.house_of_sohe.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.R;

public class TopHeadingsProductsViewHolder extends RecyclerView.ViewHolder {

    public ImageView verticalProductsImage,verticalAddButton;
    public TextView verticalProductsName,verticalProductsPrice,verticalProductsDesc;
    public CardView prodVerticalCardView;

    public TopHeadingsProductsViewHolder(@NonNull View itemView) {
        super(itemView);

        verticalProductsImage = itemView.findViewById(R.id.verticalProdImage);
        verticalAddButton = itemView.findViewById(R.id.verticalAddButton);
        verticalProductsName = itemView.findViewById(R.id.verticalProdName);
        verticalProductsDesc = itemView.findViewById(R.id.verticalProdDesc);
        verticalProductsPrice = itemView.findViewById(R.id.verticalProdPrice);
        prodVerticalCardView = itemView.findViewById(R.id.prodVerticalCardView);
    }

}