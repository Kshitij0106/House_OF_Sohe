package com.house_of_sohe;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.house_of_sohe.Common.Connectivity;
import com.house_of_sohe.Model.Users;

public class ProfileFragment extends Fragment {
    private SwipeRefreshLayout swipeProfile;
    private TextView userName, userPhoneNo, userEmail, userHouseNo, userColony, userPinCode, userCity, userLandmark;
    private ImageView editUserName, editUserPhoneNo, editAddress;

    private DatabaseReference userDetailsRef;
    private FirebaseAuth auth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        swipeProfile = view.findViewById(R.id.swipeLayoutProfile);

        userName = view.findViewById(R.id.userName);
        userPhoneNo = view.findViewById(R.id.userPhoneNo);
        userEmail = view.findViewById(R.id.userEmail);
        userHouseNo = view.findViewById(R.id.userHouseNo);
        userColony = view.findViewById(R.id.userColony);
        userPinCode = view.findViewById(R.id.userPinCode);
        userCity = view.findViewById(R.id.userCity);
        userLandmark = view.findViewById(R.id.userLandmark);
        editUserName = view.findViewById(R.id.editUserName);
        editUserPhoneNo = view.findViewById(R.id.editUserPhoneNo);
        editAddress = view.findViewById(R.id.editAddress);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final String Uid = user.getUid();

        swipeProfile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load(Uid);
            }
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAddress(user);
            }
        });

        editUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName(user);
            }
        });

        editUserPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPhone(user);
            }
        });

        load(Uid);
        return view;
    }

    private void load(String Uid){
        if(Connectivity.isConnectedToInternet(getActivity())){
            swipeProfile.setRefreshing(false);
            showDetails(Uid);
        }else{
            Toast.makeText(getActivity(), "Please Check Your Connection !", Toast.LENGTH_LONG).show();
        }
    }

    private void showDetails(final String Uid) {
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
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editAddress(FirebaseUser user) {
        final String uid = user.getUid();
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
                    userDetailsRef.child(uid).child("userHouseNo").setValue(cHouse);
                    userDetailsRef.child(uid).child("userColony").setValue(cColony);
                    userDetailsRef.child(uid).child("userPinCode").setValue(cPinCode);
                    userDetailsRef.child(uid).child("userCity").setValue(cCity);
                    userDetailsRef.child(uid).child("userLandmark").setValue(cLandmark);

                    Toast.makeText(getActivity(), "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                    addDialog.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new ProfileFragment()).addToBackStack(null).commit();
                }
            }
        });
        addDialog.show();

    }

    private void editName(FirebaseUser user) {
        final String uid = user.getUid();
        final Dialog nameDialog = new Dialog(getActivity(), R.style.Address_Dialog);
        nameDialog.setContentView(R.layout.details_name_edit_dialog);
        final EditText newName;
        final Button confirm;

        newName = nameDialog.findViewById(R.id.newName);
        confirm = nameDialog.findViewById(R.id.confirmName);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = newName.getText().toString();

                if (name.isEmpty()) {
                    newName.setError("Add Correct UserName");
                } else {
                    userDetailsRef.child(uid).child("userName").setValue(name);

                    Toast.makeText(getActivity(), "UserName Updated Successfully", Toast.LENGTH_SHORT).show();
                    nameDialog.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new ProfileFragment()).addToBackStack(null).commit();
                }
            }
        });
        nameDialog.show();
    }

    private void editPhone(FirebaseUser user){
        final String uid = user.getUid();
        final Dialog phnDialog = new Dialog(getActivity(), R.style.Address_Dialog);
        phnDialog.setContentView(R.layout.details_edit_phone_dilaog);
        final EditText newPhone;
        final Button confirm;

        newPhone = phnDialog.findViewById(R.id.newPhone);
        confirm = phnDialog.findViewById(R.id.confirmPhone);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = newPhone.getText().toString();

                if (phone.isEmpty()) {
                    newPhone.setError("Add Correct Phone No.");
                } else {
                    userDetailsRef.child(uid).child("userPhone").setValue(phone);

                    Toast.makeText(getActivity(), "Phone Number Updated Successfully", Toast.LENGTH_SHORT).show();
                    phnDialog.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.mainPage, new ProfileFragment()).addToBackStack(null).commit();
                }
            }
        });
        phnDialog.show();
    }
}


