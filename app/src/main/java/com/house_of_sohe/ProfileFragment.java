package com.house_of_sohe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.house_of_sohe.Model.Users;

public class ProfileFragment extends Fragment {
    public TextView userName, userPhoneNo, userEmail, userHouseNo, userColony, userPinCode, userCity, userState, userLandmark;
    private ImageView editUserName, editUserPhoneNo, editAddress;

    private DatabaseReference userDetailsRef;
    private FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userName);
        userPhoneNo = view.findViewById(R.id.userPhoneNo);
        userEmail = view.findViewById(R.id.userEmail);
        userHouseNo = view.findViewById(R.id.userHouseNo);
        userColony = view.findViewById(R.id.userColony);
        userPinCode = view.findViewById(R.id.userPinCode);
        userCity = view.findViewById(R.id.userCity);
//        userState = view.findViewById(R.id.userState);
        userLandmark = view.findViewById(R.id.userLandmark);
        editUserName = view.findViewById(R.id.editUserName);
        editUserPhoneNo = view.findViewById(R.id.editUserPhoneNo);
        editAddress = view.findViewById(R.id.editAddress);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final String Uid = user.getUid();

        userDetailsRef = FirebaseDatabase.getInstance().getReference("Users");
        userDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Users users = data.getValue(Users.class);

                    String uid = users.getUid();
                    if (uid.equals(Uid)) {
                        userName.setText(users.getUserName());
                        userPhoneNo.setText(users.getUserPhone());
                        userEmail.setText(users.getUserEmail());
                        userHouseNo.setText(users.getUserHouseNo());
                        userColony.setText(users.getUserColony());
                        userPinCode.setText(users.getUserPinCode());
                        userCity.setText(users.getUserCity());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}
