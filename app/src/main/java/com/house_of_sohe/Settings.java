package com.house_of_sohe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.house_of_sohe.Common.Session;

public class Settings extends Fragment implements View.OnClickListener{
    private CardView myOrdersCardView, myProfileCardView, logOutCardView, deleteAccountCardView, contactUsCardView, aboutUsCardView;

    private DatabaseReference userWishListRef, userCartRef;
    private FirebaseAuth auth;

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        myOrdersCardView = view.findViewById(R.id.myOrdersCardView);
        myProfileCardView = view.findViewById(R.id.myProfileCardView);
        logOutCardView = view.findViewById(R.id.logOutCardView);
        deleteAccountCardView = view.findViewById(R.id.deleteAccountCardView);
        contactUsCardView = view.findViewById(R.id.contactUsCardView);
        aboutUsCardView = view.findViewById(R.id.aboutUsCardView);

        myOrdersCardView.setOnClickListener(this);
        myProfileCardView.setOnClickListener(this);
        logOutCardView.setOnClickListener(this);
        deleteAccountCardView.setOnClickListener(this);
        contactUsCardView.setOnClickListener(this);
        aboutUsCardView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == myOrdersCardView){
            getFragmentManager().beginTransaction().replace(R.id.mainPage,new OrdersFragment()).addToBackStack("").commit();
        }else if(view == myProfileCardView){
            getFragmentManager().beginTransaction().replace(R.id.mainPage,new ProfileFragment()).addToBackStack("").commit();

        }else if(view == logOutCardView){
            AlertDialog.Builder logOutDilaog = new AlertDialog.Builder(getActivity());
            logOutDilaog.setMessage("LogOut !");
            logOutDilaog.setCancelable(false);
            logOutDilaog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Session session = new Session(getActivity());
                    session.setLoggedIn(false);
                    Toast.makeText(getActivity(), "LogOut Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            logOutDilaog.show();

        }else if(view == deleteAccountCardView){
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
            deleteDialog.setCancelable(false);
            deleteDialog.setMessage("Delete Your Account");
            deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    String uid = user.getUid();

                    userWishListRef = FirebaseDatabase.getInstance().getReference("WishList").child(uid);
                    userCartRef = FirebaseDatabase.getInstance().getReference("Cart").child(uid);
                    userWishListRef.removeValue();
                    userCartRef.removeValue();

                    Session session = new Session(getActivity());
                    session.setLoggedIn(false);
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    user.delete();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            deleteDialog.show();

        }else if(view == contactUsCardView){

        }else if(view == aboutUsCardView){

        }
    }
}
