package com.example.itravel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
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

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;

public class PostAddFragment extends Fragment {
    EditText titleEt;
    EditText descriptionEt;
    EditText locationEt;
    //difficulty-ADD
    //Picture-ADD
    Button saveBtn;
    Button cancelBtn;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        titleEt = view.findViewById(R.id.addpost_title_show);
        descriptionEt = view.findViewById(R.id.addpost_description_show);
        locationEt = view.findViewById(R.id.addpost_location_show);
         saveBtn=view.findViewById(R.id.addpost_save_btn);
        cancelBtn=view.findViewById(R.id.addpost_cancel_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                //navigate to home page
                //Navigation.findNavController(v).navigateUp();
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


    private void save(){

        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        String title = titleEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String location = locationEt.getText().toString();

        Post post = new Post(title,description,location);

        Model.instance.addPost(post,()->{
            Navigation.findNavController(titleEt).navigateUp();
        });


    }

}