package com.example.itravel.post;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.HomePageFragment;
import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


public class PostDetailsFragment extends Fragment {

    TextView titleEt;
    TextView locationEt;
    TextView authorEt;
    TextView descriptionEt;
    TextView difficultyEt;
    Button editBtn;
    Button deleteBtn;
    Post p;
    String postId;
    ProgressBar progressBar;
    ImageView postImg;
    User usr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        progressBar = view.findViewById(R.id.postdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        titleEt = view.findViewById(R.id.postdetails_title_tv);
        locationEt = view.findViewById(R.id.postdetails_location_tv);
        authorEt = view.findViewById(R.id.postdetails_author_tv);
        descriptionEt=view.findViewById(R.id.postdetails_description_tv);
        difficultyEt = view.findViewById(R.id.postdetails_difficulty_tv);
        editBtn = view.findViewById(R.id.postdetails_edit_btn);
        deleteBtn = view.findViewById(R.id.postdetails_delete_btn);
        postImg = view.findViewById(R.id.postDetails_post_img);
        postImg.setVisibility(View.GONE);

        postId = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostId();

        Model.instance.getPostByTitle(postId, new Model.GetPostByTitle() {
            @Override
            public void onComplete(Post post) {

                savePost(post.getTitle(), post.getLocation(), post.getDescription(),post.getDifficulty(), post.getUserName(), post);
                p=post;

            }
        });


        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Model.instance.getUserById(id, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                usr = user;
                Log.d("TAG", "length of list  -  " + user.getPostList().size());
                for (String id :user.getPostList()) {
                    if(id.equals(postId))
                    {
                        editBtn.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Navigation.findNavController(v).navigate(PostDetailsFragmentDirections.actionPostDetailsFragmentToPostEditFragment(postId));
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.instance.deletePost(p, new Model.deletePost() {
                    @Override
                    public void onComplete() {
                        Toast toast = new Toast(getContext());
                        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window, null);
                        TextView toastText = popupView.findViewById(R.id.popup_text_tv);
                        toastText.setText("Post deleted!");
                        toastText.setTextSize(20);
                        toast.setView(popupView);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        Model.instance.refreshPostList();
                        Navigation.findNavController(v).navigateUp();
                    }
                });
            }
        });

        return view;
    }

    public void savePost(String title, String location, String description,String difficulty, String userName,Post post)
    {
//
//        editBtn.setEnabled(false);
//        deleteBtn.setEnabled(false);
        titleEt.setText(title);
        locationEt.setText(location);
        authorEt.setText(userName);
        descriptionEt.setText(description);
        difficultyEt.setText(difficulty);
        if(!post.getPhoto().contentEquals("")) {
            Picasso.get()
                    .load(post.getPhoto())
                    .into(postImg);
        }
        postImg.setVisibility(View.VISIBLE);

    }

}