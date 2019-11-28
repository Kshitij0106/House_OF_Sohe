package com.house_of_sohe;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.house_of_sohe.Common.Connectivity;
import com.house_of_sohe.Common.Session;
import com.house_of_sohe.Model.Products;
import com.house_of_sohe.ViewHolder.TopHeadingsProductsViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TopHeadingsFragment extends Fragment {
    private SwipeRefreshLayout swipeTopHeadings;
    private TextView topHeadingsCategoryName;
    private ImageView topHeadingsToHome,topHeadingsShowCategory;
    private Dialog showCategory;

    private RecyclerView topHeadingsProductsRecyclerView;
    private DatabaseReference topHeadingsProductsRef;
    private FirebaseRecyclerOptions<Products> topHeadingsProductsOptions;
    private FirebaseRecyclerAdapter<Products, TopHeadingsProductsViewHolder> topHeadingsProductsAdapter;

    private ArrayList<Products> topHeadingsProductsList;

    private Session session;
    private TextView closeText;
    private ImageView addToCart, addToWishList;
    private Dialog addToDialog;


    private DatabaseReference wishListReference,cartReference;
    private FirebaseAuth auth;

    public TopHeadingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle title = this.getArguments();
        final String heading = title.getString("heading");

        View view = inflater.inflate(R.layout.fragment_top_headings, container, false);
        swipeTopHeadings = view.findViewById(R.id.swipeLayoutTopHeadings);

        topHeadingsCategoryName = view.findViewById(R.id.topHeadingsCategoryName);
        topHeadingsToHome = view.findViewById(R.id.topHeadingsToHome);
        topHeadingsShowCategory = view.findViewById(R.id.topHeadingsShowCategories);

        topHeadingsProductsRecyclerView = view.findViewById(R.id.topHeadingsProductsRecyclerView);
        topHeadingsProductsRecyclerView.setHasFixedSize(true);

        session = new Session(getActivity());
        wishListReference = FirebaseDatabase.getInstance().getReference("WishList");
        cartReference = FirebaseDatabase.getInstance().getReference("Cart");
        auth = FirebaseAuth.getInstance();

        topHeadingsProductsList = new ArrayList<>();

        if (heading.equals("Best From Our Collection") || heading.equals("Latest Arrivals") || heading.equals("Trending")) {
            topHeadingsProductsRef = FirebaseDatabase.getInstance().getReference("TopHeadings Products").child(heading).child("Items");
        } else {
            topHeadingsProductsRef = FirebaseDatabase.getInstance().getReference("Products").child(heading).child("Items");
            topHeadingsShowCategory.setVisibility(View.VISIBLE);
        }

        if(heading.equals("Best From Our Collection")){
            topHeadingsCategoryName.setText("Best Collection");
        }else{
            topHeadingsCategoryName.setText(heading);
        }

        swipeTopHeadings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load(heading);
            }
        });
        load(heading);
        return view;
    }

    private void load(final String heading){
        if(Connectivity.isConnectedToInternet(getActivity())){
            swipeTopHeadings.setRefreshing(false);
            topHeadingsProductsOptions = new FirebaseRecyclerOptions.Builder<Products>().setQuery(topHeadingsProductsRef, Products.class).build();
            topHeadingsProductsAdapter = new FirebaseRecyclerAdapter<Products, TopHeadingsProductsViewHolder>(topHeadingsProductsOptions) {
                @Override
                protected void onBindViewHolder(@NonNull TopHeadingsProductsViewHolder topHeadingsProductsViewHolder, int i, @NonNull final Products products) {
                    Picasso.get().load(products.getProdImg()).fit().centerCrop().into(topHeadingsProductsViewHolder.verticalProductsImage);
                    topHeadingsProductsViewHolder.verticalProductsName.setText(products.getProdName());
                    topHeadingsProductsViewHolder.verticalProductsDesc.setText(products.getProdDesc());

                    Locale locale = new Locale("en","IN");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    int amt = Integer.parseInt(products.getProdPrice());
                    topHeadingsProductsViewHolder.verticalProductsPrice.setText(fmt.format(amt));

                    topHeadingsProductsViewHolder.verticalAddButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setAddToDialog(products.getProdImg(), products.getProdPrice(), products.getProdName(),products.getProdCode());

                            addToDialog.show();
                            addToDialog.setCancelable(false);
                        }
                    });

                    topHeadingsProductsViewHolder.prodVerticalCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle prodBundle = new Bundle();
                            prodBundle.putString("title",heading);
                            prodBundle.putString("prodCode",products.getProdCode());
                            ProductDetailsFragment pdf = new ProductDetailsFragment();
                            pdf.setArguments(prodBundle);
                            getFragmentManager().beginTransaction().replace(R.id.mainPage, pdf).addToBackStack("").commit();
                        }
                    });
                }

                @NonNull
                @Override
                public TopHeadingsProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_vertical, parent, false);
                    TopHeadingsProductsViewHolder tpvh = new TopHeadingsProductsViewHolder(view1);
                    return tpvh;
                }
            };

            topHeadingsProductsRecyclerView.setHasFixedSize(true);
            topHeadingsProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            topHeadingsProductsRecyclerView.setAdapter(topHeadingsProductsAdapter);
            topHeadingsProductsAdapter.startListening();
            topHeadingsProductsAdapter.notifyDataSetChanged();

            topHeadingsShowCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTopHeadingsShowCategory();
                }
            });

            topHeadingsToHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().beginTransaction().replace(R.id.mainPage,new HomePageFragment()).commit();
                }
            });
        }else{
            Toast.makeText(getActivity(), "Please Check Your Connection !", Toast.LENGTH_LONG).show();
        }
    }

    public void setTopHeadingsShowCategory(){
        showCategory = new Dialog(getActivity());
        final TextView cancelText, blouseCategory, kurtaCategory, suitCategory, pantsCategory, lehengaCategory, dressesCategory, palazzoCategory;

        showCategory.setContentView(R.layout.category_popup_layout_dialog);
        cancelText = showCategory.findViewById(R.id.cancelText);
        blouseCategory = showCategory.findViewById(R.id.blouseCategory);
        kurtaCategory = showCategory.findViewById(R.id.kurtaCategory);
        suitCategory = showCategory.findViewById(R.id.suitCategory);
        pantsCategory = showCategory.findViewById(R.id.pantsCategories);
        lehengaCategory = showCategory.findViewById(R.id.lehengaCategory);
        dressesCategory = showCategory.findViewById(R.id.dressesCategory);
        palazzoCategory = showCategory.findViewById(R.id.palazzoCategory);

        blouseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(blouseCategory.getText().toString());
            }
        });
        kurtaCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(kurtaCategory.getText().toString());
            }
        });
        suitCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(suitCategory.getText().toString());
            }
        });
        pantsCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(pantsCategory.getText().toString());
            }
        });
        lehengaCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(lehengaCategory.getText().toString());
            }
        });
        dressesCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(dressesCategory.getText().toString());
            }
        });
        palazzoCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategory(palazzoCategory.getText().toString());
            }
        });

        topHeadingsShowCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory.show();
                showCategory.setCancelable(false);
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory.dismiss();
            }
        });
    }

    public void openCategory(String CategoryName){
        showCategory.dismiss();
        Bundle categoryBundle = new Bundle();
        categoryBundle.putString("heading",CategoryName);

        TopHeadingsFragment topHeadingsFragment = new TopHeadingsFragment();
        topHeadingsFragment.setArguments(categoryBundle);
        getFragmentManager().beginTransaction().replace(R.id.mainPage, topHeadingsFragment).addToBackStack("").commit();
    }

    public void setAddToDialog(final String img, final String price, final String name, final String code) {
            final FirebaseUser user = auth.getCurrentUser();
            final String uid = user.getUid();

            addToDialog = new Dialog(getActivity(), R.style.Dialog);
            addToDialog.setContentView(R.layout.add_to_layout_dialog);

            closeText = addToDialog.findViewById(R.id.closeText);
            addToCart = addToDialog.findViewById(R.id.addToCartDialog);
            addToWishList = addToDialog.findViewById(R.id.addToWishListDialog);

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (session.isLoggedIn()) {
                        Products products = new Products();
                        products.setProdName(name);
                        products.setProdPrice(price);
                        products.setProdCode(code);
                        products.setProdImg(img);
                        products.setProdSize("M");
                        products.setProdQty("1");

                        cartReference.child(uid).child(code).setValue(products);

                        Toast.makeText(getActivity(), "Added To Cart", Toast.LENGTH_SHORT).show();
                        addToDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Log In First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            addToWishList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(session.isLoggedIn()) {
                        Products prod = new Products();
                        prod.setProdName(name);
                        prod.setProdImg(img);
                        prod.setProdCode(code);
                        prod.setProdPrice(price);
                        prod.setProdQty("1");
                        prod.setProdSize("M");

                        wishListReference.child(uid).child(code).setValue(prod);

                        Toast.makeText(getActivity(), "Added to WishList", Toast.LENGTH_SHORT).show();
                        addToDialog.dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Log In First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            closeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToDialog.dismiss();
                }
            });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (topHeadingsProductsAdapter != null) {
            topHeadingsProductsAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (topHeadingsProductsAdapter != null) {
            topHeadingsProductsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (topHeadingsProductsAdapter != null) {
            topHeadingsProductsAdapter.stopListening();
        }
    }
}