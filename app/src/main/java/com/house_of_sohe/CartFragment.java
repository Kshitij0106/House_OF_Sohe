package com.house_of_sohe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.house_of_sohe.Common.Connectivity;
import com.house_of_sohe.Model.Orders;
import com.house_of_sohe.Model.Products;
import com.house_of_sohe.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements View.OnClickListener {
    private SwipeRefreshLayout swipeCart;
    private TextView cartSubTotal, cartDeliveryPrice, cartTotalPrice, emptyCart, showNow, goToOrders;
    private ImageView cartToWishList, clearCart;
    private Button cartVerifyDetails, cartPlaceOrder;
    private RecyclerView cartRecyclerview;

    private FirebaseRecyclerOptions<Products> cartOptions;
    private FirebaseRecyclerAdapter<Products, CartViewHolder> cartAdapter;
    private DatabaseReference cartReference;

    private FirebaseAuth auth;

    private DatabaseReference userRef, ordersRef;
    private List<Products> productsList;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_cart, container, false);

        swipeCart = view.findViewById(R.id.swipeLayoutCart);
        cartToWishList = view.findViewById(R.id.cartToWishList);
        clearCart = view.findViewById(R.id.clearCart);
        emptyCart = view.findViewById(R.id.emptyCart);
        showNow = view.findViewById(R.id.shopNow);
        goToOrders = view.findViewById(R.id.goToOrders);

        cartSubTotal = view.findViewById(R.id.cartSubTotal);
        cartDeliveryPrice = view.findViewById(R.id.cartDeliveryPrice);
        cartTotalPrice = view.findViewById(R.id.cartTotalPrice);
        productsList = new ArrayList<>();

        cartVerifyDetails = view.findViewById(R.id.cartVerifyDetails);
        cartPlaceOrder = view.findViewById(R.id.cartPlaceOrder);

        cartToWishList.setOnClickListener(this);
        clearCart.setOnClickListener(this);
        cartVerifyDetails.setOnClickListener(this);
        showNow.setOnClickListener(this);
        goToOrders.setOnClickListener(this);

        cartRecyclerview = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerview.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final String uid = user.getUid();

        cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(uid);

        swipeCart.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                checkCart();
            }
        });

        loadData();
        checkCart();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("userHouseNo").exists() || !dataSnapshot.child("userColony").exists() ||
                        !dataSnapshot.child("userPinCode").exists() || !dataSnapshot.child("userCity").exists()) {
                    setAddressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cartPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Orders orders = new Orders(user.getEmail(), productsList,cartTotalPrice.getText().toString());
                ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(uid);

                ordersRef.setValue(orders);

                Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_SHORT).show();

                cartReference.removeValue();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();

            }
        });

        return view;
    }

    private void loadData() {
        if (Connectivity.isConnectedToInternet(getActivity())) {
            swipeCart.setRefreshing(false);
            cartOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(cartReference, Products.class).build();
            cartAdapter = new FirebaseRecyclerAdapter<Products, CartViewHolder>(cartOptions) {
                @Override
                protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull final Products products) {
                    Picasso.get().load(products.getProdImg()).fit().centerCrop().into(cartViewHolder.imageInCart);
                    cartViewHolder.nameInCart.setText(products.getProdName());
                    cartViewHolder.qtyInCart.setNumber(products.getProdQty());
                    cartViewHolder.sizeInCart.setText(products.getProdSize());

                    Locale locale = new Locale("en", "IN");
                    final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                    int prodAmt = Integer.parseInt(products.getProdQty()) * Integer.parseInt(products.getProdPrice());
                    cartViewHolder.priceInCart.setText(format.format(prodAmt));

                    cartViewHolder.selectSize.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setSizeDialog(products.getProdCode());
                        }
                    });

                    cartViewHolder.qtyInCart.setRange(0, 5);
                    if (cartViewHolder.qtyInCart.getNumber().equals("0")) {
                        cartReference.child(products.getProdCode()).removeValue();
                        total();
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
                            total();
                        }
                    });
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    String currDate = sdf.format(new Date());
                    String oID = String.valueOf(System.currentTimeMillis());

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
                                    total();
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

                    productsList.add(new Products(products.getProdImg(), products.getProdName(), products.getProdPrice(),
                            products.getProdQty(), products.getProdSize(), currDate, oID));

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
        } else {
            Toast.makeText(getActivity(), "Please Check Your Connection !", Toast.LENGTH_LONG).show();
        }
        total();
    }

    private void checkCart() {
        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    emptyCart.setVisibility(View.VISIBLE);
                    showNow.setVisibility(View.VISIBLE);
                    goToOrders.setVisibility(View.VISIBLE);
                    cartPlaceOrder.setEnabled(false);
                    clearCart.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void total() {
        cartReference.addValueEventListener(new ValueEventListener() {
            int amt = 0;
            int delivery=70;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String price = data.child("prodPrice").getValue(String.class);
                    String qty = data.child("prodQty").getValue(String.class);
                    amt+=Integer.parseInt(price)*Integer.parseInt(qty);
                }
                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cartSubTotal.setText(fmt.format(amt));
                if(amt == 0){
                    delivery = 0;
                    cartDeliveryPrice.setText(fmt.format(delivery));
                }else {
                    cartDeliveryPrice.setText(fmt.format(delivery));
                }
                cartTotalPrice.setText(fmt.format(amt + delivery));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setSizeDialog(final String code) {
        final Dialog sizeDialog = new Dialog(getActivity(), R.style.Category_Dialog);
        sizeDialog.setContentView(R.layout.size_layout_dialog);
        final TextView smallSize, mediumSize, largeSize, extraLargeSize, doubleExtraLargeSize, closeSizeDialog;

        smallSize = sizeDialog.findViewById(R.id.smallSizeCategory);
        mediumSize = sizeDialog.findViewById(R.id.mediumSizeCategory);
        largeSize = sizeDialog.findViewById(R.id.largeSizeCategory);
        extraLargeSize = sizeDialog.findViewById(R.id.extraLargeSizeCategory);
        doubleExtraLargeSize = sizeDialog.findViewById(R.id.doubleExtraLargeSizeCategory);
        closeSizeDialog = sizeDialog.findViewById(R.id.closeSizeDialog);

        sizeDialog.setCancelable(false);
        sizeDialog.show();

        closeSizeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeDialog.dismiss();
            }
        });

        smallSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartReference.child(code).child("prodSize").setValue("S");
                sizeDialog.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
            }
        });
        mediumSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartReference.child(code).child("prodSize").setValue("M");
                sizeDialog.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
            }
        });
        largeSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartReference.child(code).child("prodSize").setValue("L");
                sizeDialog.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
            }
        });
        extraLargeSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartReference.child(code).child("prodSize").setValue("XL");
                sizeDialog.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
            }
        });
        doubleExtraLargeSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartReference.child(code).child("prodSize").setValue("XXL");
                sizeDialog.dismiss();
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void setAddressDialog() {
        final Dialog addDialog = new Dialog(getActivity(), R.style.Address_Dialog);
        addDialog.setContentView(R.layout.address_layout_dialog);
        final EditText completeHouseNo, completeColony, completePinCode, completeCity, completeLandmark;
        final Button confirm;

        completeHouseNo = addDialog.findViewById(R.id.completeHouseNo);
        completeColony = addDialog.findViewById(R.id.completeColony);
        completePinCode = addDialog.findViewById(R.id.completePinCode);
        completeCity = addDialog.findViewById(R.id.completeCity);
        completeLandmark = addDialog.findViewById(R.id.completeLandmark);
        confirm = addDialog.findViewById(R.id.confirmAddress);

        cartPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.show();
                addDialog.setCancelable(false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cHouse = completeHouseNo.getText().toString();
                String cColony = completeColony.getText().toString();
                String cPinCode = completePinCode.getText().toString();
                String cCity = completeCity.getText().toString();
                String cLandmark = completeLandmark.getText().toString();

                if (cHouse.isEmpty()) {
                    completeHouseNo.setError("Add Correct House No");
                }
                if (cColony.isEmpty()) {
                    completeColony.setError("Add Correct Colony");
                }
                if (cPinCode.isEmpty()) {
                    completePinCode.setError("Add Correct Pin Code");
                }
                if (cCity.isEmpty()) {
                    completeCity.setError("Add Correct City");
                } else {
                    userRef.child("userHouseNo").setValue(cHouse);
                    userRef.child("userColony").setValue(cColony);
                    userRef.child("userPinCode").setValue(cPinCode);
                    userRef.child("userCity").setValue(cCity);
                    userRef.child("userLandmark").setValue(cLandmark);

                    Toast.makeText(getActivity(), "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                    addDialog.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack(null).commit();
                }
            }
        });
    }

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
            case R.id.shopNow:
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).commit();
                break;
            case R.id.goToOrders:
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new OrdersFragment()).commit();
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

