package com.example.itravel.post;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;

import org.w3c.dom.Text;

public class PostDetailsFragment extends Fragment {

    TextView titleEt;
    TextView locationEt;
    TextView authorEt;
    TextView descriptionEt;
    TextView difficultyEt;
    Button editBtn;
    Button deleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        titleEt = view.findViewById(R.id.postdetails_title_tv);
        locationEt = view.findViewById(R.id.postdetails_location_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt=view.findViewById(R.id.postdetails_description_tv);
        difficultyEt = view.findViewById(R.id.postdetails_difficulty_tv);
        editBtn = view.findViewById(R.id.postdetails_edit_btn);
        deleteBtn = view.findViewById(R.id.postdetails_delete_btn);

        String postTitle = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostTitle();
        Log.d("TAG", "~~~~~POST TITLE:~~~~~" + postTitle); // why it gives back the location?

//        Post post =  Model.instance.GetPostByTitle(postTitle, new Model.GetPostByTitle() {
//            @Override
//            public void onComplete(Post post) {
//
//            }
//        });
//
//        titleEt.setText(post.getTitle());
//        titleEt.setTextSize(20);
//
//        locationEt.setText(post.getLocation());
//        locationEt.setTextSize(20);
//
//        authorEt.setText(post.getUserName());
//        authorEt.setTextSize(20);
//
//        descriptionEt.setText(post.getDescription());
//        descriptionEt.setTextSize(20);

        //LIKES + DIFFICULTY - ADD !!


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: edit post fragment
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}