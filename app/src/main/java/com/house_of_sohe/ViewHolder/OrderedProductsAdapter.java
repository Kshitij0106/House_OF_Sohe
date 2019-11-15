package com.house_of_sohe.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.house_of_sohe.Model.Products;
import com.house_of_sohe.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderedProductsAdapter extends RecyclerView.Adapter<OrderedProductsAdapter.OrderedProductsViewHolder> {

    private Context context;
    private List<Products> prods;

    public OrderedProductsAdapter(Context context, List<Products> prods) {
        this.context = context;
        this.prods = prods;
    }

    @NonNull
    @Override
    public OrderedProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
        OrderedProductsViewHolder opvh = new OrderedProductsViewHolder(view);
        return opvh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedProductsViewHolder holder, int position) {
        holder.ordersProdName.setText(prods.get(position).getProdName());
        holder.ordersProdDate.setText(prods.get(position).getProdDate());
        holder.ordersProdID.setText(prods.get(position).getOID());
        Picasso.get().load(prods.get(position).getProdImg()).fit().centerCrop().into(holder.ordersProdImage);
    }

    @Override
    public int getItemCount() {
        return prods.size();
    }

    public class OrderedProductsViewHolder extends RecyclerView.ViewHolder {
        public TextView ordersProdName, ordersProdID, ordersProdDate;
        public ImageView ordersProdImage;

        public OrderedProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            ordersProdName = itemView.findViewById(R.id.ordersProdName);
            ordersProdID = itemView.findViewById(R.id.ordersProdID);
            ordersProdDate = itemView.findViewById(R.id.ordersProdDate);
            ordersProdImage = itemView.findViewById(R.id.ordersProdImage);
        }
    }
}
