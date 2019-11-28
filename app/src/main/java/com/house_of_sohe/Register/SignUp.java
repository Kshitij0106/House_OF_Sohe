package com.house_of_sohe.Register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.house_of_sohe.Common.Connectivity;
import com.house_of_sohe.Common.Session;
import com.house_of_sohe.HomePageFragment;
import com.house_of_sohe.Model.Users;
import com.house_of_sohe.R;

public class SignUp extends Fragment {
    private EditText userNameSign, emailSign, phnSign, passSign, cnfrmSign;
    private TextView skip;
    private Button signUp;
    private Session session;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    private FirebaseAuth auth;
    private DatabaseReference reference;


    public SignUp() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        userNameSign = view.findViewById(R.id.usernameSign);
        emailSign = view.findViewById(R.id.emailSign);
        phnSign = view.findViewById(R.id.phnSign);
        passSign = view.findViewById(R.id.pswrdSign);
        cnfrmSign = view.findViewById(R.id.cnfrmSign);
        skip = view.findViewById(R.id.skip);
        signUp = view.findViewById(R.id.signUpButton);
        session = new Session(getActivity());
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        preferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        edit = preferences.edit();

        if (session.isLoggedIn()) {
            goToHomePage();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Connectivity.isConnectedToInternet(getActivity())) {

                    final String userName = userNameSign.getText().toString();
                    final String email = emailSign.getText().toString();
                    final String phone = phnSign.getText().toString();
                    String pass = passSign.getText().toString();
                    String cnfrmpass = cnfrmSign.getText().toString();

                    if (userName.isEmpty() && email.isEmpty() && phone.isEmpty() && pass.isEmpty() && cnfrmpass.isEmpty()) {
                        userNameSign.setError("Enter Your Name");
                        emailSign.setError("Enter Your Email ID");
                        phnSign.setError("Enter Your Phone Number");
                        passSign.setError("Enter Your Password");
                        cnfrmSign.setError("Confirm Your Password");
                    } else if (userName.isEmpty()) {
                        userNameSign.setError("Enter Your Name");
                    } else if (email.isEmpty()) {
                        emailSign.setError("Enter Your Email ID");
                    } else if (phone.isEmpty()) {
                        phnSign.setError("Enter Your Phone Number");
                    } else if (pass.isEmpty()) {
                        passSign.setError("Enter Your Password");
                    } else if (cnfrmpass.isEmpty()) {
                        cnfrmSign.setError("Confirm Your Password");
                    } else if (!pass.equals(cnfrmpass)) {
                        Toast.makeText(getActivity(), "Password does not match", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                session.setLoggedIn(true);

                                FirebaseUser user = auth.getCurrentUser();
                                String uid = user.getUid();
                                edit.putString("uid",uid).commit();

                                Users info = new Users();
                                info.setUid(uid);
                                info.setUserName(userName);
                                info.setUserEmail(email);
                                info.setUserPhone(phone);

                                reference.child(uid).setValue(info);

                                goToHomePage();

                                userNameSign.setText("");
                                emailSign.setText("");
                                phnSign.setText("");
                                passSign.setText("");
                                cnfrmSign.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomePage();
            }
        });

        return view;
    }

    public void goToHomePage() {
        getFragmentManager().beginTransaction().replace(R.id.mainPage,new HomePageFragment()).addToBackStack("").commit();
        Toast.makeText(getActivity(), "Welcome", Toast.LENGTH_SHORT).show();
    }

}
