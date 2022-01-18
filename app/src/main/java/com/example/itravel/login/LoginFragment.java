package com.example.itravel.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.MainActivity;
import com.example.itravel.R;
import com.example.itravel.model.Model;

public class LoginFragment extends Fragment {

    EditText emailEt;
    EditText passwordEt;
    Button loginBtn;
    Button signupBtn;
    SharedPreferences.Editor Ed;
    View popupView;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        SharedPreferences sp = context.getSharedPreferences("Login", 0);
//        Ed = sp.edit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailEt = view.findViewById(R.id.login_email_et);
        passwordEt = view.findViewById(R.id.login_password_et);
        loginBtn=view.findViewById(R.id.login_login_btn);
        signupBtn = view.findViewById(R.id.login_signup_btn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                if(email.equals("") || password.equals(""))
                {
                    Toast toast = new Toast(getContext());
                    View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window, null);
                    TextView toastText = popupView.findViewById(R.id.popup_text_tv);
                    toastText.setText("You did not enter all values!");
                    toastText.setTextSize(20);
                    toast.setView(popupView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    loginBtn.setEnabled(true);
                }
                else {
                    Model.instance.isExist(getContext(), email, password, () -> {
                        Log.d("TAG", "Connected!!!");
                        toFeedActivity();
                    });
                }
            }
        });
        signupBtn.setOnClickListener(Navigation.createNavigateOnClickListener(LoginFragmentDirections.actionLoginFragment3ToSignUpFragment()));

        return view;
    }

    private void toFeedActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}