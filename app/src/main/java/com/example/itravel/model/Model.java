package com.example.itravel.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {

    public static final Model instance = new Model();
    //added
    public Executor executor= Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    ModelFirebase modelFirebase = new ModelFirebase();
    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();

    FirebaseUser currUser;


    public enum PostListLoadingState{
        loading,
        loaded
    }
    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();


    private Model(){

    }

    public interface AddUserListener{
        void onComplete();
    }

    public interface AddPostListener{
        void onComplete();
    }

    public interface UpdateUserListener{
        void onComplete();
    }

    public void updateUser(User user, UpdateUserListener listener){
        modelFirebase.updateUser(user,listener);
    }

    //posts
    public LiveData<List<Post>> getAll(){
        if (postsList.getValue() == null) { refreshPostList(); };
        return  postsList;
    }

    public LiveData<List<Post>> getAllPostsByUser(User user) {
        if (postsList.getValue() == null) { refreshPostListByUser(user); };
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

    public void refreshPostListByUser(User user) {
        postListLoadingState.setValue(PostListLoadingState.loading);
        modelFirebase.getAllPostsByUser(user, new ModelFirebase.GetAllPostsByUserListener() {

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


    public interface GetUserById {
        void onComplete(User user);
    }

    public interface GetPostByTitle {
        void onComplete(Post post);
    }

    public User getUserById(String Id, GetUserById listener) {
        modelFirebase.getUserById(Id, listener);
        return null;
    }
    public User getPostByTitle(String postTitle, GetPostByTitle listener) {
        modelFirebase.getPostByTitle(postTitle,listener);
        return null;
    }

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    /**
     * Authentication
     */

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }

    public interface CreateNewAccount{
        void onComplete();
    }

    public void createNewAccount(String name, String email, String password, String photo, CreateNewAccount listener) {
        modelFirebase.createNewAccount(name, email, password, photo, listener);
    }

    public interface SignOut{
        void onComplete();
    }

    public void signOut(SignOut listener) {
        modelFirebase.signOut(listener);
    }

    public interface IsExist{
        void onComplete();
//        void onFailure();
    }

    public void isExist(String email, String password, IsExist listener) {
        modelFirebase.isExist(email, password, listener);
    }
}
