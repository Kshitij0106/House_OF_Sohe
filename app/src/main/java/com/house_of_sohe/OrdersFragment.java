package com.house_of_sohe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.house_of_sohe.ViewHolder.OrderedProductsAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private SwipeRefreshLayout swipeOrders;
    private TextView emptyOrders, shopNow;
    private RecyclerView ordersRecylerView;
    private DatabaseReference ordersRef;
    private List<Products> productsList;

    private FirebaseAuth auth;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        swipeOrders = view.findViewById(R.id.swipeLayoutOrders);

        emptyOrders = view.findViewById(R.id.emptyOrders);
        shopNow = view.findViewById(R.id.shopNowFromOrders);

        ordersRecylerView = view.findViewById(R.id.ordersRecyclerView);
        ordersRecylerView.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final String Uid = user.getUid();
        productsList = new ArrayList<>();

        swipeOrders.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load(Uid);
            }
        });
        load(Uid);

        return view;
    }
    public  void load(String Uid){
        if(Connectivity.isConnectedToInternet(getActivity())){
            swipeOrders.setRefreshing(false);
            ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(Uid);
            ordersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Orders orders = dataSnapshot.getValue(Orders.class);

                    if(!dataSnapshot.child("productsList").exists()){
                        emptyOrders.setVisibility(View.VISIBLE);
                        shopNow.setVisibility(View.VISIBLE);
                        shopNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getFragmentManager().beginTransaction().replace(R.id.mainPage,new HomePageFragment()).commit();
                            }
                        });
                    }else{
                        productsList = orders.getProductsList();

                        OrderedProductsAdapter adapter = new OrderedProductsAdapter(getActivity(),productsList);
                        ordersRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ordersRecylerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(getActivity(), "Please Check Your Connection !", Toast.LENGTH_LONG).show();
        }
    }
}