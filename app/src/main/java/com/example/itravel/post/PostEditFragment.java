package com.example.itravel.post;


import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PostEditFragment extends Fragment {

    TextView titleEt;
    TextView locationEt;
    TextView descriptionEt;
    TextView titleTv;
    TextView locationTv;
    TextView descriptionTv;
    TextView difficulityTv;
    TextView picturesTv;

    Spinner dropdown;
    Button saveBtn;
    Button cancelBtn;
    String postId;
    ProgressBar progressBar;
    ImageButton cameraBtn;
    ImageButton galleryBtn;
    String photo = "";
    Bitmap imageBitmap;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);
        progressBar = view.findViewById(R.id.editpost_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.editpost_title_show);
        locationEt = view.findViewById(R.id.editpost_location_show);
        descriptionEt=view.findViewById(R.id.editpost_description_show);
        dropdown = view.findViewById(R.id.editpost_dropdown);
        saveBtn = view.findViewById(R.id.editpost_save_btn);
        cancelBtn = view.findViewById(R.id.editpost_cancel_btn);
        cameraBtn = view.findViewById(R.id.editpost_camera_btn);
        galleryBtn = view.findViewById(R.id.editpost_gallery_btn);
        titleTv = view.findViewById(R.id.editpost_title_tv);
        locationTv = view.findViewById(R.id.editpost_location_tv);
        descriptionTv = view.findViewById(R.id.editpost_description_tv);
        difficulityTv = view.findViewById(R.id.editpost_difficulty_tv);
        picturesTv = view.findViewById(R.id.editpost_pictures_tv);

        updateUI(View.INVISIBLE);

        postId = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostId();

        Model.instance.getPostById(postId, new Model.GetPostById() {
            @Override
            public void onComplete(Post post) {
                displayPost(post.getTitle(), post.getLocation(), post.getDescription(),post.getDifficulty());
                progressBar.setVisibility(View.GONE);
                updateUI(View.VISIBLE);
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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.instance.getPostById(postId, new Model.GetPostById() {
                    @Override
                    public void onComplete(Post post) {
                        String difficulty = dropdown.getSelectedItem().toString();
                        savePost(post,titleEt.getText().toString(), descriptionEt.getText().toString(), locationEt.getText().toString(),difficulty, view);
                    }
                });
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        return view;
    }

    private void savePost(Post post, String title,String description,String location,String difficulty, View v) {
        if(title.equals("") || location.equals(""))
        {
            Toast toast = new Toast(getContext());
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window, null);
            TextView toastText = popupView.findViewById(R.id.popup_text_tv);
            toastText.setText("You didn't enter title/location! ");
            toastText.setTextSize(20);
            toast.setView(popupView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
            saveData(post, title, description , location , difficulty,  v);
    }

    private void saveData(Post post,String title,String description,String location,String difficulty, View v) {
        post.setTitle(title);
        post.setLocation(location);
        post.setDescription(description);
        post.setDifficulty(difficulty);

        if (imageBitmap != null) {
            Model.instance.savePostImage(imageBitmap, post.getId() + ".jpg", new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    post.setPhoto(url);
                    Log.d("TAG", "url - " + url);
                    Model.instance.updatePost(post);
                    Model.instance.UpdatePost(post, () -> {});
                    Model.instance.addPhotoToPost(post, new Model.AddPhotoToPost() {
                        @Override
                        public void onComplete() {

                        }
                    });
                    Model.instance.refreshPostList();
                    Navigation.findNavController(v).navigateUp();
                }
            });
        }else {
            Model.instance.updatePost(post);
            Model.instance.UpdatePost(post, () -> {});
            Navigation.findNavController(v).navigateUp();
        }
    }

    public void displayPost(String title, String location, String description,String difficulty)
    {
        int pos=0;
        titleEt.setText(title);
        titleEt.setTextSize(20);

        locationEt.setText(location);
        locationEt.setTextSize(20);

        descriptionEt.setText(description);
        descriptionEt.setTextSize(20);

        String[] itemsDropdown = new String[]{"easy", "medium", "hard"};
        for(int i=0; i< itemsDropdown.length;i++)
        {
            if(itemsDropdown[i].equals(difficulty)) {
                pos = i;
                i=itemsDropdown.length-1;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemsDropdown);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(pos);
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

    public void updateUI(int type) {
        titleEt.setVisibility(type);
        locationEt.setVisibility(type);
        descriptionEt.setVisibility(type);
        dropdown.setVisibility(type);
        saveBtn.setVisibility(type);
        cancelBtn.setVisibility(type);
        cameraBtn.setVisibility(type);
        galleryBtn.setVisibility(type);
        titleTv.setVisibility(type);
        locationTv.setVisibility(type);
        descriptionTv.setVisibility(type);
        difficulityTv.setVisibility(type);
        picturesTv.setVisibility(type);
    }
}