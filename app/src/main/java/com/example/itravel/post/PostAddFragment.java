package com.example.itravel.post;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PostAddFragment extends Fragment {
    EditText titleEt;
    EditText descriptionEt;
    EditText locationEt;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    Button saveBtn;
    Button cancelBtn;
    View view;
    Bitmap imageBitmap;

    String userEmail;
    String userPassword;

    Spinner dropdown;
    String difficulty;

    User user;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
        userEmail = sp1.getString("email", null);
        userPassword = sp1.getString("password", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_post_add, container, false);
        titleEt = view.findViewById(R.id.addpost_title_show);
        descriptionEt = view.findViewById(R.id.addpost_description_show);
        locationEt = view.findViewById(R.id.addpost_location_show);
        saveBtn = view.findViewById(R.id.addpost_save_btn);
        cancelBtn = view.findViewById(R.id.addpost_cancel_btn);
        dropdown = view.findViewById(R.id.addpost_dropdown);
        cameraBtn = view.findViewById(R.id.editpost_camera_btn);
        galleryBtn = view.findViewById(R.id.addpost_gallery_btn);


        String[] itemsDropdown = new String[]{"easy", "medium", "hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemsDropdown);
        dropdown.setAdapter(adapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView spinner_item_text = (TextView) view;
                if(spinner_item_text!=null)
                {
                    difficulty = spinner_item_text.getText().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficulty = "";
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

    private void save() {
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        String title = titleEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String location = locationEt.getText().toString();
        String photo = "";

        String Id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(title.equals("") || location.equals("") )
        {
            Toast toast = new Toast(getContext());
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window, null);
            TextView toastText = popupView.findViewById(R.id.popup_text_tv);
            toastText.setText("You didn't enter Title/Location!");
            toastText.setTextSize(20);
            toast.setView(popupView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            saveBtn.setEnabled(true);
            cancelBtn.setEnabled(true);
            return;
        }

        Model.instance.getUserById(Id, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                String nickName = user.getNickName();
                String postId = savePost(nickName, title, description, photo, location, difficulty, user);
                user.addNewPost(postId);
            }
        });
    }

    public String savePost(String name, String title, String description, String photo, String location, String difficulty, User user) {
        Post post = new Post("", name, title, description, photo, location, difficulty);

        if (imageBitmap != null) {

            Model.instance.addPost(post, user, (id) -> {
                post.setId(id);
                Model.instance.savePostImage(imageBitmap, post.getId() + ".jpg", new Model.SaveImageListener() {
                    @Override
                    public void onComplete(String url) {
                        post.setPhoto(url);
                        Log.d("TAG", "url - " + url);
                        Model.instance.updatePost(post);
                        Model.instance.addPhotoToPost(post, new Model.AddPhotoToPost() {
                            @Override
                            public void onComplete() {
                                Log.d("TAG", "added photo successfully");
                            }
                        });
                        Model.instance.refreshPostList();
                        Navigation.findNavController(view).navigate(PostAddFragmentDirections.actionPostAddFragmentToHomePageFragment2());
                    }
                });

            });
        } else {
            Model.instance.addPost(post, user, (id) -> {
                post.setId(id);
                Model.instance.refreshPostList();
                Navigation.findNavController(view).navigate(PostAddFragmentDirections.actionPostAddFragmentToHomePageFragment2());
            });
        }
        return post.getId();
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