package com.house_of_sohe.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.Model.Products;
import com.house_of_sohe.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.productsViewHolder> {

    private Context context;
    private List<Products> productsList;


    public ProductsAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public productsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        productsViewHolder pvh = new productsViewHolder(view);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final productsViewHolder holder, final int position) {
        holder.prodDesc.setText(productsList.get(position).getProdDesc());
        holder.prodName.setText(productsList.get(position).getProdName());
        Picasso.get().load(productsList.get(position).getProdImg()).fit().centerCrop().into(holder.prodImage);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class productsViewHolder extends RecyclerView.ViewHolder {
        public ImageView prodImage, addToButton;
        public TextView prodName, prodPrice, prodDesc;

        public productsViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prodImage);
            addToButton = itemView.findViewById(R.id.addToButton);
            prodName = itemView.findViewById(R.id.prodName);
            prodPrice = itemView.findViewById(R.id.prodPrice);
            prodDesc = itemView.findViewById(R.id.prodDescription);

            addToButton.setVisibility(View.INVISIBLE);

        }
    }
}
