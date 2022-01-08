package com.example.itravel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailEt = view.findViewById(R.id.login_email_show);
        passwordEt = view.findViewById(R.id.login_password_show);
        loginBtn=view.findViewById(R.id.login_login_btn);
        signupBtn = view.findViewById(R.id.login_signup_btn);

        loginBtn.setOnClickListener(Navigation.createNavigateOnClickListener(LoginFragmentDirections.actionLoginFragmentToHomePageFragment()));

        signupBtn.setOnClickListener(Navigation.createNavigateOnClickListener(LoginFragmentDirections.actionLoginFragmentToSignUpFragment()));

        return view;
    }
}