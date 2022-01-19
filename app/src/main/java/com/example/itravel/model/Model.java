package com.example.itravel.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.itravel.MyApplication;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {

    public static final Model instance = new Model();
    //added
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    ModelFirebase modelFirebase = new ModelFirebase();
    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> currUserPostsList = new MutableLiveData<List<Post>>();

    FirebaseUser currUser;


    public enum PostListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();


    private Model() {

    }

    /**
     * Interface
     **/

    public interface AddPostListener {
        void onComplete(String id);
    }

    public interface UpdateUserListener {
        void onComplete();
    }

    public interface GetUserById {
        void onComplete(User user);
    }

    public interface UpdatePostListener {
        void onComplete();
    }

    public interface GetPostByTitle {
        void onComplete(Post post);
    }

    public interface CreateNewAccount {
        void onComplete();
    }

    public interface deletePost {
        void onComplete();
    }

    public interface SignOut {
        void onComplete();
    }

    public interface IsExist {
        void onComplete();
    }

    /******************************************************/

    public void deleteAllPostsDao() {
        executor.execute(() -> {
            List<Post> pList = AppLocalDb.db.postDao().getAll();
            for (Post post : pList) {
                AppLocalDb.db.postDao().delete(post);
            }
        });
    }


    /**
     * Posts
     **/

    public void refreshPostList() {

        postListLoadingState.setValue(PostListLoadingState.loading);
        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> pList = AppLocalDb.db.postDao().getAll();
            postsList.postValue(pList);
        });
        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDb.db.postDao().insertAll(post);
                            if (lud < post.getUpdateDate()) {
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Post> pList = AppLocalDb.db.postDao().getAll();
                        postsList.postValue(pList);
                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }


    public void refreshPostListByUser(String name) {

        postListLoadingState.setValue(PostListLoadingState.loading);
        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> pList = AppLocalDb.db.postDao().getAllPostsByUser(name);
            currUserPostsList.postValue(pList);
        });
        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lud = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDb.db.postDao().insertAll(post);
                            if (lud < post.getUpdateDate()) {
                                lud = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostsLastUpdateDate", lud)
                                .commit();

                        //return all data to caller
                        List<Post> pList = AppLocalDb.db.postDao().getAllPostsByUser(name);
                        currUserPostsList.postValue(pList);
                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }


    public void updatePost(Post post) {
        executor.execute(() -> {
            AppLocalDb.db.postDao().update(post);
        });
    }

    public LiveData<List<Post>> getAll() {
        if (postsList.getValue() == null) {
            refreshPostList();
        }
        ;
        return postsList;
    }

    public void addPost(Post post, AddPostListener listener) {
        modelFirebase.addPost(post, listener);
    }

    public User getPostByTitle(String postId, GetPostByTitle listener) {
        modelFirebase.getPostByTitle(postId, listener);
        return null;
    }

    public void deletePost(Post post, deletePost listener) {
        executor.execute(() -> {
            AppLocalDb.db.postDao().delete(post);
        });
        modelFirebase.deletePost(post, listener);
    }

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    public void UpdatePost(Post post, UpdatePostListener listener) {
        modelFirebase.updatePost(post, listener);
    }

    /**
     * Users
     **/

    public void getUserById(String Id, GetUserById listener) {
        modelFirebase.getUserById(Id, listener);
    }

    public void updateUser(String id, String newName, UpdateUserListener listener) {
        modelFirebase.updateUser(id, newName, listener);
    }
//
//    public void refreshPostListByUser(User user) {
//        postListLoadingState.setValue(PostListLoadingState.loading);
//        Log.d("TAG", "222");
//        modelFirebase.getAllPostsByUser(user.getName(), new ModelFirebase.GetAllPostsByUserListener() {
//            @Override
//            public void onComplete(List<Post> list) {
//                currUserPostsList.setValue(list);
//                postListLoadingState.setValue(PostListLoadingState.loaded);
//            }
//        });
//    }

    /**
     * Authentication
     **/

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }

    public void createNewAccount(String name, String email, String password, String photo, CreateNewAccount listener) {
        modelFirebase.createNewAccount(name, email, password, photo, listener);
    }

    public void signOut(SignOut listener) {
        modelFirebase.signOut(listener);
    }

    public void isExist(Context context, String email, String password, IsExist listener) {
        modelFirebase.isExist(context, email, password, listener);
    }
}
