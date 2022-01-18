package com.example.itravel.post;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.itravel.MyApplication;
import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.Calendar;

public class PostEditFragment extends Fragment {

    TextView titleEt;
    TextView locationEt;
    TextView descriptionEt;
    Spinner dropdown;
    Button saveBtn;
    Button cancelBtn;
    String postTitle;
    Post post;

    String oldTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_edit, container, false);
        titleEt = view.findViewById(R.id.editpost_title_show);
        locationEt = view.findViewById(R.id.editpost_location_show);
        descriptionEt=view.findViewById(R.id.editpost_description_show);
        dropdown = view.findViewById(R.id.editpost_dropdown);
        saveBtn = view.findViewById(R.id.editpost_save_btn);
        cancelBtn = view.findViewById(R.id.editpost_cancel_btn);

        postTitle = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostTitle();

        Model.instance.getPostByTitle(postTitle, new Model.GetPostByTitle() {
            @Override
            public void onComplete(Post post) {
                displayPost(post.getTitle(), post.getLocation(), post.getDescription(),post.getDifficulty());
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model.instance.getPostByTitle(postTitle, new Model.GetPostByTitle() {
                    @Override
                    public void onComplete(Post post) {
                        savePost(post,titleEt.getText().toString(), descriptionEt.getText().toString(), locationEt.getText().toString(), post.getDifficulty());
                        Model.instance.refreshPostList();
                        Navigation.findNavController(v).navigateUp();
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

    private void savePost(Post post, String title,String description,String location,String difficulty ) {

        saveData(post, title, description , location , difficulty);
        Model.instance.updatePost(post);
        Model.instance.UpdatePost(post, () -> {

        });
    }

    private void saveData(Post post,String title,String description,String location,String difficulty) {
        post.setTitle(title);
        Log.d("TAG", " %%%%% " + title);
        post.setLocation(location);
//      post.setDifficulty("easy");
        post.setDescription(description);
        String[] itemsDropdown = new String[]{"easy", "medium", "hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemsDropdown);
        dropdown.setAdapter(adapter);
//      dropdown.getSelectedItem().toString();
        adapter.notifyDataSetChanged();
        post.setDifficulty( dropdown.getSelectedItem().toString());

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
}