package com.example.itravel.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Model {

    public static final Model instance = new Model();
    ModelFirebase modelFirebase = new ModelFirebase();
    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    public enum PostListLoadingState{
        loading,
        loaded
    }
    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();


    private Model(){
//        for(int i=0;i<data.size();i++){
//            User u = new User("name", "" + i, "phone", "address");
//            data.add(u);
//        }
    }

    public interface AddUserListener{
        void onComplete();
    }

    public interface AddPostListener{
        void onComplete();
    }

    public void addUser(User user, AddUserListener listener){
        modelFirebase.addUser(user,listener);
    }

    //posts
    public LiveData<List<Post>> getAll(){
        if (postsList.getValue() == null) { refreshPostList(); };
        return  postsList;
    }

    public void refreshPostList() {
        postListLoadingState.setValue(PostListLoadingState.loading);


        modelFirebase.getAllPosts(new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                postsList.setValue(list);
                postListLoadingState.setValue(PostListLoadingState.loaded);
            }
        });
    }

    public void addPost(Post post, AddPostListener listener){
        modelFirebase.addPost(post,listener);
    }


    public interface GetUserByEmail {
        void onComplete(User user);
    }
    public interface GetPostByTitle {
        void onComplete(Post post);
    }

    public User getUserByEmail(String userEmail, GetUserByEmail listener) {
        modelFirebase.getUserByEmail(userEmail,listener);
        return null;
    }
    public User getPostByTitle(String postTitle, GetPostByTitle listener) {
        modelFirebase.getPostByTitle(postTitle,listener);
        return null;
    }

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }


}
