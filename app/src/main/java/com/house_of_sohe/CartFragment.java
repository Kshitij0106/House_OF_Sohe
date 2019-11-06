package com.house_of_sohe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
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
import com.house_of_sohe.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements View.OnClickListener {
    private TextView cartToWishList, clearCart, cartSubTotal, cartDeliveryPrice, cartTotalPrice, emptyCart, showNow;
    private Button cartVerifyDetails, cartPlaceOrder;
    private RecyclerView cartRecyclerview;

    private FirebaseRecyclerOptions<Products> cartOptions;
    private FirebaseRecyclerAdapter<Products, CartViewHolder> cartAdapter;
    private DatabaseReference cartReference;

    private FirebaseAuth auth;

    private List<Integer> subTotalAmt;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartToWishList = view.findViewById(R.id.cartToWishList);
        clearCart = view.findViewById(R.id.clearCart);
        emptyCart = view.findViewById(R.id.emptyCart);
        showNow = view.findViewById(R.id.shopNow);

        cartSubTotal = view.findViewById(R.id.cartSubTotal);
        cartDeliveryPrice = view.findViewById(R.id.cartDeliveryPrice);
        cartTotalPrice = view.findViewById(R.id.cartTotalPrice);
        subTotalAmt = new ArrayList<>();

        cartVerifyDetails = view.findViewById(R.id.cartVerifyDetails);
        cartPlaceOrder = view.findViewById(R.id.cartPlaceOrder);

        cartToWishList.setOnClickListener(this);
        clearCart.setOnClickListener(this);
        cartVerifyDetails.setOnClickListener(this);
        cartPlaceOrder.setOnClickListener(this);
        showNow.setOnClickListener(this);

        cartRecyclerview = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerview.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uid = user.getUid();

        cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(uid);
        cartOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(cartReference, Products.class).build();
        cartAdapter = new FirebaseRecyclerAdapter<Products, CartViewHolder>(cartOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull final Products products) {
                Picasso.get().load(products.getProdImg()).fit().centerCrop().into(cartViewHolder.imageInCart);
                cartViewHolder.nameInCart.setText(products.getProdName());
                cartViewHolder.qtyInCart.setNumber(products.getProdQty());

                Locale locale = new Locale("en", "IN");
                final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                final int prodAmt = Integer.parseInt(products.getProdQty()) * Integer.parseInt(products.getProdPrice());
                cartViewHolder.priceInCart.setText(format.format(prodAmt));

                subTotalAmt.add(prodAmt);

                if (products.getProdSize().equals("")) {
                    cartViewHolder.sizeInCart.setText("");
                } else {
                    cartViewHolder.sizeInCart.setText(products.getProdSize());
                }

                cartViewHolder.qtyInCart.setRange(0, 100);
                if (cartViewHolder.qtyInCart.getNumber().equals("0")) {
                    cartReference.child(products.getProdCode()).removeValue();
                }
                cartViewHolder.qtyInCart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        String newQty = cartViewHolder.qtyInCart.getNumber();
                        cartViewHolder.qtyInCart.setNumber(String.valueOf(newValue));
                        String price = products.getProdPrice();
                        int amt = Integer.parseInt(newQty) * Integer.parseInt(price);
                        cartViewHolder.priceInCart.setText(format.format(amt));

                        cartReference.child(products.getProdCode()).child("prodQty").setValue(newQty);
                    }
                });

                cartViewHolder.removeFromCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder removeDialog = new AlertDialog.Builder(getActivity());
                        removeDialog.setMessage("Are you sure you want to remove " + products.getProdName() + " from Cart");
                        removeDialog.setCancelable(false);
                        removeDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cartReference.child(products.getProdCode()).removeValue();
                                Toast.makeText(getActivity(), "Removed From Cart", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        removeDialog.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
                CartViewHolder cvh = new CartViewHolder(view1);
                return cvh;
            }
        };

        cartRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerview.setAdapter(cartAdapter);
        cartAdapter.startListening();

        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    emptyCart.setVisibility(View.VISIBLE);
                    showNow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        total(subTotalAmt);

        return view;
    }

//    public void total(List<Integer> subTotalAmt) {
//        int subtotal=0;
//        if(subtotal == 0){
//            cartSubTotal.setText("");
//            cartDeliveryPrice.setText("");
//            cartTotalPrice.setText("");
//        }
//        for (int i = 0; i <= subTotalAmt.size(); i++) {
//            subtotal += subTotalAmt.get(i);
//        }
//        Locale locale = new Locale("en", "IN");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
//        cartSubTotal.setText(fmt.format(subtotal));
//        cartDeliveryPrice.setText(fmt.format(99));

//        String cartSubTotalAmt = cartSubTotal.getText().toString();
//        String cartDeliveryAmt = cartDeliveryPrice.getText().toString();
//        int cartTotalAmt = Integer.parseInt(cartSubTotalAmt) + Integer.parseInt(cartDeliveryAmt);
//
//        cartTotalPrice.setText(fmt.format(cartTotalAmt));
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cartToWishList:
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new WishList()).addToBackStack("").commit();
                break;
            case R.id.clearCart:
                AlertDialog.Builder clearDialog = new AlertDialog.Builder(getActivity());
                clearDialog.setMessage("Are you sure you want to clear the Cart");
                clearDialog.setCancelable(false);
                clearDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cartReference.removeValue();
                        Toast.makeText(getActivity(), "Cart Is Now Empty", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                clearDialog.show();
                break;
            case R.id.cartVerifyDetails:
                Toast.makeText(getActivity(), "bdj", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cartPlaceOrder:
                Toast.makeText(getActivity(), "njdsnvk", Toast.LENGTH_SHORT).show();
                break;
            case R.id.shopNow:
                getFragmentManager().beginTransaction().replace(R.id.mainPage,new HomePageFragment()).commit();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cartAdapter != null)
            cartAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null)
            cartAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cartAdapter != null)
            cartAdapter.stopListening();
    }
}
