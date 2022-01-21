package com.example.itravel;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;


public class SignUpFragment extends Fragment {

    EditText nameEt;
    EditText emailEt;
    EditText passwordEt;
    EditText verifyPasswordEt;
    ImageButton img;
    Button saveBtn;
    Button cancelBtn;
    View view;
    TextView popup;
    View popupView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        nameEt = view.findViewById(R.id.signup_name_show);
        emailEt = view.findViewById(R.id.signup_email_show);
        passwordEt = view.findViewById(R.id.signup_password_show);
        verifyPasswordEt = view.findViewById(R.id.signup_verifypass_show);
        img = view.findViewById(R.id.signup_image_gallery);
        saveBtn = view.findViewById(R.id.signup_save_btn);
        cancelBtn = view.findViewById(R.id.signup_cancel_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(inflater);
//                Navigation.findNavController(v).navigate(SignUpFragmentDirections.actionSignUpFragmentToHomePageFragment());
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });



        return view;
    }


    private void save(LayoutInflater inflater) {
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

        String name = nameEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        String verifyPassword = verifyPasswordEt.getText().toString();
        String photo = "";
        popupView = inflater.inflate(R.layout.popup_window, null);
        popup = popupView.findViewById(R.id.popup_text_tv);

        if (name.equals("") || email.equals("") || password.equals("") || verifyPassword.equals("")) {
            //popup
            popup.setText("You did not enter all values!");
            popup.setTextSize(20);
            onButtonShowPopupWindowClick(view, inflater);
            saveBtn.setEnabled(true);
            cancelBtn.setEnabled(true);
            return;
        }
        if (!password.equals(verifyPassword)) {
            //popup
            popup.setText("Passwords does not match!");
            popup.setTextSize(20);
            onButtonShowPopupWindowClick(view, inflater);
            saveBtn.setEnabled(true);
            cancelBtn.setEnabled(true);
            return;
        }

        Model.instance.createNewAccount(name, email, password, photo, () -> {
            toFeedActivity();
        });

//        User user = new User(name, email, password);

//        Model.instance.addUser(user, () -> {
//          //  Navigation.findNavController(nameEt).navigate(SignUpFragmentDirections.actionSignUpFragmentToHomePageFragment());
//            toFeedActivity();
//        });

    }

    public void onButtonShowPopupWindowClick(View view, LayoutInflater inflater) {

        // inflate the layout of the popup window
//        LayoutInflater inflater = (LayoutInflater)
//                getSystemService(LAYOUT_INFLATER_SERVICE);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void toFeedActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}