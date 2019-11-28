package com.house_of_sohe.Register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.house_of_sohe.Common.Connectivity;
import com.house_of_sohe.Common.Session;
import com.house_of_sohe.HomePageFragment;
import com.house_of_sohe.R;

public class LogIn extends Fragment {
    private EditText email, pass;
    private TextView createAcc, skip;
    private Button LogIn;
    private Session session;
    private FirebaseAuth auth;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public LogIn() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        email = view.findViewById(R.id.emailLogin);
        pass = view.findViewById(R.id.passLogin);
        createAcc = view.findViewById(R.id.createAccount);
        skip = view.findViewById(R.id.skipfornow);
        LogIn = view.findViewById(R.id.logInButton);
        session = new Session(getActivity());
        auth = FirebaseAuth.getInstance();

        preferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editor = preferences.edit();

        if (session.isLoggedIn()) {
            goToHomePage();
        }
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Connectivity.isConnectedToInternet(getActivity())) {

                    String id = email.getText().toString();
                    String pswrd = pass.getText().toString();

                    if (id.isEmpty() && pswrd.isEmpty()) {
                        email.setError("Enter Email Id");
                        pass.setError("Enter Password");
                    } else if (id.isEmpty()) {
                        email.setError("Enter Email Id");
                    } else if (pswrd.isEmpty()) {
                        pass.setError("Enter Password");
                    } else {
                        auth.signInWithEmailAndPassword(id, pswrd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                session.setLoggedIn(true);

                                FirebaseUser user = auth.getCurrentUser();
                                String uid = user.getUid();
                                editor.putString("uid",uid);
                                editor.commit();

                                goToHomePage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pass.setText("");
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        email.setText("");
                        pass.setText("");
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.mainPage, new SignUp()).addToBackStack("").commit();
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
        getFragmentManager().beginTransaction().replace(R.id.mainPage, new HomePageFragment()).commit();
        Toast.makeText(getActivity(), "Welcome", Toast.LENGTH_SHORT).show();
    }

}
