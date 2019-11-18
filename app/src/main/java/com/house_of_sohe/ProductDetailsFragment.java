package com.house_of_sohe;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.house_of_sohe.Model.BannerImages;
import com.house_of_sohe.Model.Products;
import com.house_of_sohe.Transformer.ImageTransformer;
import com.house_of_sohe.ViewHolder.ViewPagerAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProductDetailsFragment extends Fragment {
    private ViewPager prodImagesViewPager;
    private List<BannerImages> imagesList;
    private DatabaseReference imagesRef;

    private DatabaseReference detailsRef;
    private ImageView prodDetailsToCart, verticalGoToButton, prodDetailsAddToWishList;
    private TextView prodDetailsName, prodDetailsPrice, prodDetailsDesc, prodDetailsInfo;
    private TextView textSizeSmall, textSizeMedium, textSizeLarge, textSizeXL, textSizeXXL;
    private Button prodDetailsAddToCart;

    private DatabaseReference cartRef, wishListRef;
    private FirebaseAuth auth;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle prodRef = this.getArguments();
        String heading = prodRef.getString("title");
        String prodCode = prodRef.getString("prodCode");

        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        prodDetailsToCart = view.findViewById(R.id.prodDetailsToCart);
        verticalGoToButton = view.findViewById(R.id.verticalGoToButton);
        prodDetailsName = view.findViewById(R.id.prodDetailsName);
        prodDetailsPrice = view.findViewById(R.id.prodDetailsPrice);
        prodDetailsDesc = view.findViewById(R.id.prodDetailsDesc);
        prodDetailsInfo = view.findViewById(R.id.prodDetailsInfo);
        textSizeSmall = view.findViewById(R.id.textSizeSmall);
        textSizeMedium = view.findViewById(R.id.textSizeMedium);
        textSizeLarge = view.findViewById(R.id.textSizeLarge);
        textSizeXL = view.findViewById(R.id.textSizeXL);
        textSizeXXL = view.findViewById(R.id.textSizeXXL);
        prodDetailsAddToCart = view.findViewById(R.id.prodDetailsAddToCart);
        prodDetailsAddToWishList = view.findViewById(R.id.prodDetailsAddToWishList);
        auth = FirebaseAuth.getInstance();

        prodImagesViewPager = view.findViewById(R.id.prodImagesViewPager);
        prodImagesViewPager.setPageTransformer(true, new ImageTransformer());
        imagesList = new ArrayList<>();

        if (heading.equals("Best From Our Collection") || heading.equals("Latest Arrivals") || heading.equals("Trending")) {
            imagesRef = FirebaseDatabase.getInstance().getReference("TopHeadings Products").child(heading).child("Items").child(prodCode).child("images");
            detailsRef = FirebaseDatabase.getInstance().getReference("TopHeadings Products").child(heading).child("Items").child(prodCode);
        } else {
            imagesRef = FirebaseDatabase.getInstance().getReference("Products").child(heading).child("Items").child(prodCode).child("images");
            detailsRef = FirebaseDatabase.getInstance().getReference("Products").child(heading).child("Items").child(prodCode);
        }

        loadImages();
        loadData();
        setButtons();
        setSize();

        return view;
    }

    private void loadImages() {
        imagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    imagesList.add(data.getValue(BannerImages.class));
                }
                ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(), imagesList);
                prodImagesViewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        FirebaseUser user = auth.getCurrentUser();
        final String uid = user.getUid();
        detailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Products prod = dataSnapshot.getValue(Products.class);
                prodDetailsName.setText(prod.getProdName());
                prodDetailsDesc.setText(prod.getProdDesc());
//                prodDetailsInfo.setText(prod.getProdInfo());

                String a = prod.getProdInfo();
                String b = a.replace("\\n", "\n");
                prodDetailsInfo.setText(b);

                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                int price = Integer.parseInt(prod.getProdPrice());
                prodDetailsPrice.setText(fmt.format(price));

                prodDetailsAddToWishList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        wishListRef = FirebaseDatabase.getInstance().getReference("WishList").child(uid);
                        addProducts(prod.getProdName(), prod.getProdCode(), prod.getProdImg(), prod.getProdPrice(), wishListRef);

                    }
                });
                prodDetailsAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(uid);
                        addProducts(prod.getProdName(), prod.getProdCode(), prod.getProdImg(), prod.getProdPrice(), cartRef);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSize(){
        textSizeSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSizeSmall.setSelected(true);
                textSizeSmall.setBackgroundColor(Color.RED);

                setSelected(textSizeMedium);
                setBackgroundColor(textSizeMedium);
                setSelected(textSizeLarge);
                setBackgroundColor(textSizeLarge);
                setSelected(textSizeXL);
                setBackgroundColor(textSizeXL);
                setSelected(textSizeXXL);
                setBackgroundColor(textSizeXXL);
            }
        });
        textSizeMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSizeMedium.setSelected(true);
                textSizeMedium.setBackgroundColor(Color.RED);

                setSelected(textSizeSmall);
                setBackgroundColor(textSizeSmall);
                setSelected(textSizeLarge);
                setBackgroundColor(textSizeLarge);
                setSelected(textSizeXL);
                setBackgroundColor(textSizeXL);
                setSelected(textSizeXXL);
                setBackgroundColor(textSizeXXL);

            }
        });
        textSizeLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSizeLarge.setSelected(true);
                textSizeLarge.setBackgroundColor(Color.RED);

                setSelected(textSizeSmall);
                setBackgroundColor(textSizeSmall);
                setSelected(textSizeMedium);
                setBackgroundColor(textSizeMedium);
                setSelected(textSizeXL);
                setBackgroundColor(textSizeXL);
                setSelected(textSizeXXL);
                setBackgroundColor(textSizeXXL);
            }
        });
        textSizeXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSizeXL.setSelected(true);
                textSizeXL.setBackgroundColor(Color.RED);

                setSelected(textSizeSmall);
                setBackgroundColor(textSizeSmall);
                setSelected(textSizeMedium);
                setBackgroundColor(textSizeMedium);
                setSelected(textSizeLarge);
                setBackgroundColor(textSizeLarge);
                setSelected(textSizeXXL);
                setBackgroundColor(textSizeXXL);
            }
        });
        textSizeXXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSizeXXL.setSelected(true);
                textSizeXXL.setBackgroundColor(Color.RED);

                setSelected(textSizeSmall);
                setBackgroundColor(textSizeSmall);
                setSelected(textSizeMedium);
                setBackgroundColor(textSizeMedium);
                setSelected(textSizeLarge);
                setBackgroundColor(textSizeLarge);
                setSelected(textSizeXL);
                setBackgroundColor(textSizeXL);
            }
        });

    }

    private void addProducts(String name, String code, String img, String price, DatabaseReference ref) {
        Products prod = new Products();
        prod.setProdName(name);
        prod.setProdCode(code);
        prod.setProdImg(img);
        prod.setProdPrice(price);
        prod.setProdQty("1");
        prod.setProdSize("M");

        if(textSizeSmall.isSelected()){
            prod.setProdSize(String.valueOf(textSizeSmall.getText().charAt(0)));
        }
        if(textSizeMedium.isSelected()){
            prod.setProdSize(String.valueOf(textSizeMedium.getText().charAt(0)));
        }
        if(textSizeLarge.isSelected()){
            prod.setProdSize(String.valueOf(textSizeLarge.getText().charAt(0)));
        }
        if(textSizeXL.isSelected()){
            prod.setProdSize(textSizeXL.getText().toString().substring(0,2));
        }
        if(textSizeXXL.isSelected()){
            prod.setProdSize(textSizeXXL.getText().toString().substring(0,3));
        }

        ref.child(code).setValue(prod);

        Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
    }

    private void setButtons() {
        prodDetailsToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new CartFragment()).addToBackStack("").commit();
            }
        });
        verticalGoToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(getActivity(), view);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.vertical_add_button, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menuHome:
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).commit();
                                return true;
                            case R.id.menuOrders:
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new OrdersFragment()).addToBackStack("").commit();
                                return true;
                            case R.id.menuWishList:
                                getFragmentManager().beginTransaction().replace(R.id.mainPage, new WishList()).addToBackStack("").commit();
                                return true;
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });
    }

    private void setSelected(TextView text){
        text.setSelected(false);
    }

    private void setBackgroundColor(TextView textColor){
        textColor.setBackgroundColor(Color.TRANSPARENT);
        textColor.setBackgroundResource(R.drawable.text_size_border);
    }



}
