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
import android.widget.TextView;
import android.widget.Toast;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;


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

        postId = PostDetailsFragmentArgs.fromBundle(getArguments()).getPostId();

        Model.instance.getPostByTitle(postId, new Model.GetPostByTitle() {
            @Override
            public void onComplete(Post post) {
                Log.d("TAG", "~~~~~POST USER:~~~~~" + post.getUserName());
                savePost(post.getTitle(), post.getLocation(), post.getDescription(),post.getDifficulty(), post.getUserName());
                p=post;
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

    public void savePost(String title, String location, String description,String difficulty, String userName)
    {
        titleEt.setText(title);
        locationEt.setText(location);
        authorEt.setText(userName);
        descriptionEt.setText(description);
        difficultyEt.setText(difficulty);

    }

}