package com.house_of_sohe.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.Model.Products;
import com.house_of_sohe.ProductDetailsFragment;
import com.house_of_sohe.R;
import com.house_of_sohe.TopHeadingsFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.productsViewHolder> {

    String code;
    private Context context;
    private List<Products> productsList;
    private OnCardClickListener adapterListener;

    public interface OnCardClickListener {
        void OnCardClicked(int position, String prodCode);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        adapterListener = onCardClickListener;
    }

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
        holder.prodCode.setText(productsList.get(position).getProdCode());
        Picasso.get().load(productsList.get(position).getProdImg()).fit().centerCrop().into(holder.prodImage);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class productsViewHolder extends RecyclerView.ViewHolder {
        public ImageView prodImage;
        public TextView prodName, prodPrice, prodDesc, prodCode;

        public productsViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prodImage);
            prodName = itemView.findViewById(R.id.prodName);
            prodPrice = itemView.findViewById(R.id.prodPrice);
            prodDesc = itemView.findViewById(R.id.prodDescription);
            prodCode = itemView.findViewById(R.id.prodCodeItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adapterListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            adapterListener.OnCardClicked(pos, prodCode.getText().toString());
                        }
                    }
                }
            });
        }
    }

}
