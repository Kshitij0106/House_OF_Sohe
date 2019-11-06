package com.house_of_sohe.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.R;

public class WishListViewHolder extends RecyclerView.ViewHolder {

    public ImageView wishListProductImage,wishListProductDelete,wishListToCart;
    public TextView wishListProductPrice;

    public WishListViewHolder(@NonNull View itemView) {
        super(itemView);

        wishListProductImage = itemView.findViewById(R.id.wishListProductImage);
        wishListProductDelete = itemView.findViewById(R.id.wishListProductDelete);
        wishListToCart = itemView.findViewById(R.id.wishListToCart);
        wishListProductPrice = itemView.findViewById(R.id.wishListProductPrice);
    }
}