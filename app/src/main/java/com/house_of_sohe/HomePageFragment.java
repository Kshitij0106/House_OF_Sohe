package com.house_of_sohe;

import android.app.Dialog;
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
import android.widget.ViewFlipper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.house_of_sohe.Model.BannerImages;
import com.house_of_sohe.Model.TopHeadings;
import com.house_of_sohe.Model.Products;
import com.house_of_sohe.ViewHolder.ProductsAdapter;
import com.house_of_sohe.ViewHolder.TopHeadingsViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomePageFragment extends Fragment {
    private TextView locationText;

    private ImageView filterCategories;
    private Dialog filterDialog;

    private DatabaseReference bannerRef;
    private ArrayList<BannerImages> images;
    private ViewFlipper imgBanner;

    private DatabaseReference topHeadingsRef;
    private RecyclerView topHeadingsRecyclerView;
    private FirebaseRecyclerAdapter<TopHeadings, TopHeadingsViewHolder> topHeadingsAdapter;
    private FirebaseRecyclerOptions<TopHeadings> topHeadingsOptions;

    private ArrayList<Products> prodList;

    private DatabaseReference categoryRef;
    private RecyclerView categoryRecyclerView;
    private FirebaseRecyclerOptions<TopHeadings> categoryOptions;
    private FirebaseRecyclerAdapter<TopHeadings, TopHeadingsViewHolder> categoryAdapter;

    private FirebaseAuth auth;
    private DatabaseReference wishListRef, cartRef, userRef;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabCart, fabWishList, fabSettings;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final String uid = user.getUid();

        fabMenu = view.findViewById(R.id.fabMenu);
        fabMenu.setClosedOnTouchOutside(true);

        images = new ArrayList<>();

        prodList = new ArrayList<>();

        locationText = view.findViewById(R.id.locationText);
        filterCategories = view.findViewById(R.id.filterCategories);

        imgBanner = view.findViewById(R.id.imgBanner);

        topHeadingsRecyclerView = view.findViewById(R.id.topHeadingsRecyclerView);
        topHeadingsRecyclerView.setHasFixedSize(true);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setHasFixedSize(true);

        showCategories();
        topHeadings();
        category();
        getImages();
        showLocation(uid);
        setFab(view,uid);

        return view;
    }

    public void showCategories() {
        filterDialog = new Dialog(getActivity(),R.style.Category_Dialog);
        final TextView cancelText, blouseCategory, kurtaCategory, suitCategory, pantsCategory, lehengaCategory, dressesCategory, palazzoCategory;

        filterDialog.setContentView(R.layout.category_popup_layout_dialog);
        cancelText = filterDialog.findViewById(R.id.cancelText);
        blouseCategory = filterDialog.findViewById(R.id.blouseCategory);
        kurtaCategory = filterDialog.findViewById(R.id.kurtaCategory);
        suitCategory = filterDialog.findViewById(R.id.suitCategory);
        pantsCategory = filterDialog.findViewById(R.id.pantsCategories);
        lehengaCategory = filterDialog.findViewById(R.id.lehengaCategory);
        dressesCategory = filterDialog.findViewById(R.id.dressesCategory);
        palazzoCategory = filterDialog.findViewById(R.id.palazzoCategory);

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

        filterCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.show();
                filterDialog.setCancelable(false);
            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.dismiss();
            }
        });
    }

    public void openCategory(String CategoryName){
        filterDialog.dismiss();
        Bundle categoryBundle = new Bundle();
        categoryBundle.putString("heading",CategoryName);

        TopHeadingsFragment topHeadingsFragment = new TopHeadingsFragment();
        topHeadingsFragment.setArguments(categoryBundle);
        getFragmentManager().beginTransaction().replace(R.id.mainPage, topHeadingsFragment).addToBackStack("").commit();
    }

    private void topHeadings() {
        topHeadingsRef = FirebaseDatabase.getInstance().getReference("TopHeadings");
        topHeadingsOptions = new FirebaseRecyclerOptions.Builder<TopHeadings>().setQuery(topHeadingsRef, TopHeadings.class).build();
        topHeadingsAdapter = new FirebaseRecyclerAdapter<TopHeadings, TopHeadingsViewHolder>(topHeadingsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final TopHeadingsViewHolder topHeadingsViewHolder, int i, @NonNull final TopHeadings topHeadings) {
                topHeadingsViewHolder.title.setText(topHeadings.getTitle());
                prodList = topHeadings.getProducts();

                if (prodList.size() > 4) {
                    topHeadingsViewHolder.seeAll.setVisibility(View.VISIBLE);
                } else {
                    topHeadingsViewHolder.seeAll.setVisibility(View.INVISIBLE);
                }

                ProductsAdapter adapter = new ProductsAdapter(getContext(), prodList);

                topHeadingsViewHolder.productsRecyclerView.setHasFixedSize(true);
                topHeadingsViewHolder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                topHeadingsViewHolder.productsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                topHeadingsViewHolder.productsRecyclerView.setNestedScrollingEnabled(false);

                topHeadingsViewHolder.seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle heading = new Bundle();
                        String title = topHeadingsViewHolder.title.getText().toString();
                        heading.putString("heading", title);
                        TopHeadingsFragment bundleTopHeadings = new TopHeadingsFragment();
                        bundleTopHeadings.setArguments(heading);
                        getFragmentManager().beginTransaction().replace(R.id.mainPage, bundleTopHeadings).addToBackStack("").commit();
                    }
                });


            }

            @NonNull
            @Override
            public TopHeadingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_headings, parent, false);
                TopHeadingsViewHolder thvh = new TopHeadingsViewHolder(view1);
                return thvh;
            }
        };

        topHeadingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        topHeadingsRecyclerView.setAdapter(topHeadingsAdapter);
        topHeadingsAdapter.startListening();
        topHeadingsAdapter.notifyDataSetChanged();
    }

    private void category() {
        categoryRef = FirebaseDatabase.getInstance().getReference("Top 4 Categories");
        categoryOptions = new FirebaseRecyclerOptions.Builder<TopHeadings>().setQuery(categoryRef, TopHeadings.class).build();
        categoryAdapter = new FirebaseRecyclerAdapter<TopHeadings, TopHeadingsViewHolder>(categoryOptions) {
            @Override
            protected void onBindViewHolder(@NonNull TopHeadingsViewHolder topHeadingsViewHolder, int i, @NonNull final TopHeadings topHeadings) {
                topHeadingsViewHolder.title.setText(topHeadings.getTitle());
                prodList = topHeadings.getProducts();

                ProductsAdapter adapter = new ProductsAdapter(getContext(), prodList);

                topHeadingsViewHolder.productsRecyclerView.setHasFixedSize(true);
                topHeadingsViewHolder.productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
                topHeadingsViewHolder.productsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                topHeadingsViewHolder.productsRecyclerView.setNestedScrollingEnabled(false);

                topHeadingsViewHolder.seeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = topHeadings.getTitle();
                        openCategory(title);
                    }
                });
            }

            @NonNull
            @Override
            public TopHeadingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_headings,parent,false);
                TopHeadingsViewHolder thvh = new TopHeadingsViewHolder(view1);
                return thvh;
            }
        };

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.startListening();
        categoryAdapter.notifyDataSetChanged();
    }

    private void getImages() {
        bannerRef = FirebaseDatabase.getInstance().getReference("Banner");
        bannerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    BannerImages imgs = data.getValue(BannerImages.class);
                    images.add(imgs);
                }
                loadInBanner(images);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadInBanner(ArrayList<BannerImages> images) {
        for (int i = 0; i < images.size(); i++) {
            String url = images.get(i).getImg();
            flipImages(url);
        }
    }

    private void flipImages(String url) {
        ImageView imageView = new ImageView(getActivity());
        Picasso.get().load(url).fit().into(imageView);
        imgBanner.addView(imageView);
        imgBanner.setFlipInterval(6000);
        imgBanner.setAutoStart(true);
        imgBanner.startFlipping();
        imgBanner.setInAnimation(getActivity(), android.R.anim.fade_in);
        imgBanner.setOutAnimation(getActivity(), android.R.anim.fade_out);
    }

    public void showLocation(String Uid) {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(Uid);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("userColony").exists()){
                    locationText.setText(dataSnapshot.child("userColony").getValue().toString());
                }else{
                    locationText.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setFab(View view1,String uid) {
        wishListRef = FirebaseDatabase.getInstance().getReference("WishList").child(uid);
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(uid);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long no = dataSnapshot.getChildrenCount();
                fabCart.setLabelText("Cart("+no+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        wishListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long no = dataSnapshot.getChildrenCount();
                fabWishList.setLabelText("WishList("+no+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabMenu = view1.findViewById(R.id.fabMenu);
        fabCart = view1.findViewById(R.id.fabCart);
        fabWishList = view1.findViewById(R.id.fabWishList);
        fabSettings = view1.findViewById(R.id.fabSettings);

        fabCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage,new CartFragment()).addToBackStack("").commit();
            }
        });

        fabWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage,new WishList()).addToBackStack("").commit();
            }
        });

        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage,new Settings()).addToBackStack("").commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (topHeadingsAdapter != null && categoryAdapter != null) {
            topHeadingsAdapter.startListening();
            categoryAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (topHeadingsAdapter != null && categoryAdapter != null) {
            topHeadingsAdapter.startListening();
            categoryAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (topHeadingsAdapter != null && categoryAdapter != null) {
            topHeadingsAdapter.stopListening();
            categoryAdapter.stopListening();
        }
    }
}
