package com.example.itravel.profile;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class ProfileEditFragment extends Fragment {

    TextView nameEt;
    TextView nickNameEt;
    Button saveBtn;
    Button cancelBtn;
    ProgressBar progressBar;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    String photo = "";
    Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    User usr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        progressBar = view.findViewById(R.id.editProfile_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        nameEt = view.findViewById(R.id.editProfile_name);
        nickNameEt = view.findViewById(R.id.editProfile_nickname_et);
        cameraBtn = view.findViewById(R.id.editProfile_camera_btn);
        galleryBtn = view.findViewById(R.id.editProfile_gallery_btn);
        saveBtn = view.findViewById(R.id.editProfile_save_btn);
        cancelBtn = view.findViewById(R.id.editProfile_cancel_btn);


        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Model.instance.getUserById(id, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                usr = user;
                nameEt.setText(usr.getFullName());
                nickNameEt.setText(usr.getNickName());
                progressBar.setVisibility(View.GONE);
            }
        });
//        String name = ProfilePageFragmentArgs.fromBundle(getArguments()).getName();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageBitmap != null) {
                    Model.instance.getUserById(id, new Model.GetUserById() {
                        @Override
                        public void onComplete(User user) {
                            usr = user;
                            Model.instance.saveUserImage(imageBitmap, user.getNickName() + ".jpg", new Model.SaveImageListener() {
                                @Override
                                public void onComplete(String url) {
                                    user.setPhoto(url);
                                    Log.d("TAG", "url - " + url);
                                    Model.instance.updateUser(id, nameEt.getText().toString(), nickNameEt.getText().toString(), url, () -> {
                                        Navigation.findNavController(v).navigateUp();

                                        Model.instance.refreshPostList();
                                    });


                                }

                            });
                        }
                    });

                } else {
                    Model.instance.getUserById(id, new Model.GetUserById() {
                        @Override
                        public void onComplete(User user) {
                            Model.instance.updateUser(id, nameEt.getText().toString(), nickNameEt.getText().toString(), user.getPhoto(), () -> {
                                Navigation.findNavController(v).navigateUp();
                            });
                        }
                    });
                    Model.instance.refreshPostList();

                }

            }
        });

//        saveBtn.setOnClickListener(v -> Model.instance.updateUser(id, nameEt.getText().toString(), new Model.UpdateUserListener() {
//            @Override
//            public void onComplete() {
//                Navigation.findNavController(v).navigateUp();
//            }
//        }));

        cancelBtn.setOnClickListener(v -> Navigation.findNavController(v).

                navigateUp());

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
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
            }
        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    imageBitmap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    Log.d("TAG", "ERRORRRR");
                    e.printStackTrace();
//                    Toast.makeText(PostImage.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}