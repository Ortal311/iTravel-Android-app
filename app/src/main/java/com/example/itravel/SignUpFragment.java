package com.example.itravel;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.model.Model;
import com.example.itravel.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;


public class SignUpFragment extends Fragment {

    EditText FullNameEt;
    EditText NickNameEt;
    EditText emailEt;
    EditText passwordEt;
    EditText verifyPasswordEt;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    Bitmap imageBitmap;
    Button saveBtn;
    Button cancelBtn;
    View view;
    TextView popup;
    View popupView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        FullNameEt = view.findViewById(R.id.signup_name_show);
        NickNameEt = view.findViewById(R.id.signup_nickname_show);
        emailEt = view.findViewById(R.id.signup_email_show);
        passwordEt = view.findViewById(R.id.signup_password_show);
        verifyPasswordEt = view.findViewById(R.id.signup_verifypass_show);
        cameraBtn = view.findViewById(R.id.signup_camera_btn);
        galleryBtn = view.findViewById(R.id.signup_gallery_btn);
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

        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        return view;
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if( resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");

            }
        }
        else if(requestCode == REQUEST_IMAGE_GALLERY)
        {
            if( resultCode == RESULT_OK) {
               try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    imageBitmap= BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void save(LayoutInflater inflater) {
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        String photo = "";
        List<String> postList = new LinkedList<>();
        popupView = inflater.inflate(R.layout.popup_window, null);
        popup = popupView.findViewById(R.id.popup_text_tv);

        String fullName = FullNameEt.getText().toString();
        String nickName= NickNameEt.getText().toString();;
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();
        String verifyPassword = verifyPasswordEt.getText().toString();

        User user = new User(fullName,nickName, email, photo, postList);

//        boolean isExist = Model.instance.isNickNameExist(nickName, new Model.IsNickNameExist() {
//            @Override
//            public void onComplete() {
//
//            }
//        });
//        if(isExist == true)
//        {
//            //popup
//            popup.setText("Nick name already taken!");
//            popup.setTextSize(20);
//            onButtonShowPopupWindowClick(view, inflater);
//            saveBtn.setEnabled(true);
//            cancelBtn.setEnabled(true);
//            return;
//        }

        //TODO: check if user name(nick name) already exist!
        if (fullName.equals("") ||nickName.equals("") || email.equals("") || password.equals("") || verifyPassword.equals("")) {
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

        if(imageBitmap!= null) {

            Model.instance.saveUserImage(imageBitmap, nickName + ".jpg",
                    new Model.SaveImageListener() {
                        @Override
                        public void onComplete(String url) {
                            user.setPhoto(url);
                            Log.d("TAG", "url - " + url);
                            Model.instance.createNewAccount(fullName,nickName, email, password, url, postList, () -> {
                                  toFeedActivity();
                            });
                        }
                    });
        }
        else{
            Model.instance.createNewAccount(fullName,nickName, email, password, photo, postList, () -> {
                toFeedActivity();
            });

        }



//        Model.instance.addUser(user, () -> {
//          //  Navigation.findNavController(nameEt).navigate(SignUpFragmentDirections.actionSignUpFragmentToHomePageFragment());
//            toFeedActivity();
//        });

    }

    public void onButtonShowPopupWindowClick(View view, LayoutInflater inflater) {

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