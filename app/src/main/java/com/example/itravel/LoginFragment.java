package com.example.itravel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

    EditText emailEt;
    EditText passwordEt;
    Button loginBtn;
    Button signupBtn;
    SharedPreferences.Editor Ed;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sp = context.getSharedPreferences("Login", 0);
        Ed = sp.edit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailEt = view.findViewById(R.id.login_email_show);
        passwordEt = view.findViewById(R.id.login_password_show);
        loginBtn=view.findViewById(R.id.login_login_btn);
        signupBtn = view.findViewById(R.id.login_signup_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
                Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToHomePageFragment());
            }
        });
        signupBtn.setOnClickListener(Navigation.createNavigateOnClickListener(LoginFragmentDirections.actionLoginFragmentToSignUpFragment()));

        return view;
    }

    public void saveUser() {
        Ed.putString("email", emailEt.getText().toString());
        Ed.commit();
    }
}