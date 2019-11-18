package com.house_of_sohe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.house_of_sohe.Model.Products;
import com.house_of_sohe.ViewHolder.WishListViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class WishList extends Fragment {
    private ImageView wishListToCart;
    private TextView wishListTotalItems, emptyWishList, showNow;

    private FirebaseAuth auth;

    private DatabaseReference wishListReference, cartRefernce;
    private RecyclerView wishListRecyclerView;
    private FirebaseRecyclerOptions<Products> wishListOptions;
    private FirebaseRecyclerAdapter<Products, WishListViewHolder> wishListAdapter;

    public WishList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wish_list, container, false);

        wishListToCart = view.findViewById(R.id.wishListToCart);
        wishListTotalItems = view.findViewById(R.id.wishListTotalItems);
        emptyWishList = view.findViewById(R.id.emptyWishList);
        showNow = view.findViewById(R.id.shopNow);
        wishListRecyclerView = view.findViewById(R.id.wishListRecyclerView);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String uid = user.getUid();
        cartRefernce = FirebaseDatabase.getInstance().getReference("Cart");

        wishListReference = FirebaseDatabase.getInstance().getReference("WishList").child(uid);
        wishListOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(wishListReference, Products.class).build();
        wishListAdapter = new FirebaseRecyclerAdapter<Products, WishListViewHolder>(wishListOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final WishListViewHolder wishListViewHolder, int i, @NonNull final Products products) {
                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                int amt = Integer.parseInt(products.getProdPrice());
                wishListViewHolder.wishListProductPrice.setText(fmt.format(amt));
                Picasso.get().load(products.getProdImg()).fit().centerCrop().into(wishListViewHolder.wishListProductImage);

                wishListViewHolder.wishListToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Products prod = new Products();
                        prod.setProdImg(products.getProdImg());
                        prod.setProdCode(products.getProdCode());
                        prod.setProdQty("1");
                        prod.setProdPrice(products.getProdPrice());
                        prod.setProdName(products.getProdName());
                        prod.setProdSize(products.getProdSize());

                        cartRefernce.child(uid).child(products.getProdCode()).setValue(prod);
                        Toast.makeText(getActivity(), "Added To Cart", Toast.LENGTH_SHORT).show();

                    }
                });
                wishListViewHolder.wishListProductDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder clearDialog = new AlertDialog.Builder(getActivity());
                        clearDialog.setMessage("Are you sure you want to remove "+ products.getProdName()+" from WishList");
                        clearDialog.setCancelable(false);
                        clearDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wishListReference.child(products.getProdCode()).removeValue();
                                Toast.makeText(getActivity(), "Removed From WishList", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        clearDialog.show();
                    }
                });
            }

            @NonNull
            @Override
            public WishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wish_list_layout, parent, false);
                WishListViewHolder wvh = new WishListViewHolder(view);
                return wvh;
            }
        };

        wishListRecyclerView.setHasFixedSize(true);
        wishListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        wishListRecyclerView.setAdapter(wishListAdapter);
        wishListAdapter.startListening();

        wishListToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack("").commit();
            }
        });

        wishListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    emptyWishList.setVisibility(View.VISIBLE);
                    showNow.setVisibility(View.VISIBLE);
                }else{
                    long num = dataSnapshot.getChildrenCount();
                    wishListTotalItems.setText(String.valueOf(num));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        showNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage,new HomePageFragment()).commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (wishListAdapter != null)
            wishListAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wishListAdapter != null)
            wishListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (wishListAdapter != null)
            wishListAdapter.stopListening();
    }
}