package com.house_of_sohe.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.house_of_sohe.R;

public class CartViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageInCart;
    public TextView nameInCart, sizeInCart, priceInCart, removeFromCart;
    public ElegantNumberButton qtyInCart;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        imageInCart = itemView.findViewById(R.id.imageInCart);
        nameInCart = itemView.findViewById(R.id.nameInCart);
        sizeInCart = itemView.findViewById(R.id.sizeInCart);
        priceInCart = itemView.findViewById(R.id.priceInCart);
        qtyInCart = itemView.findViewById(R.id.qtyInCart);
        removeFromCart = itemView.findViewById(R.id.removeFromCart);
    }
}
