package com.example.itravel.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.itravel.MyApplication;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {

    public static final Model instance = new Model();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    ModelFirebase modelFirebase = new ModelFirebase();
    MutableLiveData<List<Post>> postsList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> currUserPostsList = new MutableLiveData<List<Post>>();

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveUserImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {

        modelFirebase.saveUserImage(imageBitmap,imageName,listener);
    }

    public void savePostImage(Bitmap imageBitmap, String imageName, SaveImageListener listener) {

        modelFirebase.savePostImage(imageBitmap,imageName,listener);
    }

    public enum PostListLoadingState {
        loading,
        loaded
    }
    public enum UserPostListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();
    MutableLiveData<UserPostListLoadingState> userPostListLoadingState = new MutableLiveData<UserPostListLoadingState>();

    private Model() {}

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

    public interface GetPostById {
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

    public interface IsNickNameExist {
        void  onComplete();
        void onFail();
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
            Collections.reverse(pList);
            postsList.postValue(pList);
        });
        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, list -> {
            // add all records to the local db
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Long lud = new Long(0);
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
                    Collections.reverse(pList);
                    postsList.postValue(pList);
                    postListLoadingState.postValue(PostListLoadingState.loaded);
                }
            });
        });
    }

    public void refreshPostListByUser(String name) {
        userPostListLoadingState.setValue(UserPostListLoadingState.loading);
        // get last local update date
        Long lastUpdateDate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("PostsLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> pList = AppLocalDb.db.postDao().getAllPostsByUser(name);
            Collections.reverse(pList);
            currUserPostsList.postValue(pList);
        });
        // firebase get all updates since lastLocalUpdateDate
        modelFirebase.getAllPosts(lastUpdateDate, list -> {
            // add all records to the local db
            executor.execute(() -> {
                Long lud = new Long(0);
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
                Log.d("TAG", "pList " + pList.size());
                Collections.reverse(pList);
                currUserPostsList.postValue(pList);
                userPostListLoadingState.postValue(UserPostListLoadingState.loaded);
            });
        });
    }

    public void updatePost(Post post) {
        executor.execute(() -> AppLocalDb.db.postDao().update(post));
    }

    public LiveData<List<Post>> getAll() {
        if (postsList.getValue() == null) {
            refreshPostList();
        }
        return postsList;
    }

    public LiveData<List<Post>> getAllByUser() {
        if (currUserPostsList.getValue() == null) {
            refreshPostList();
        }
        return currUserPostsList;
    }

    public void addPost(Post post,User user, AddPostListener listener) {
        modelFirebase.addPost(post,user, listener);
    }

    public void getPostById(String postId, GetPostById listener) {
        modelFirebase.getPostById(postId, listener);
    }

    public void deletePost(Post post, deletePost listener) {
        executor.execute(() -> AppLocalDb.db.postDao().delete(post));
        modelFirebase.deletePost(post, listener);
    }

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    public LiveData<UserPostListLoadingState> getUserPostListLoadingState() {
        return userPostListLoadingState;
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

    public void updateUser(String id, String newName, String newNickName,String photo, UpdateUserListener listener) {
        modelFirebase.updateUser(id, newName,newNickName,photo, listener);
    }

    public void getAllPostsByUser(User user, ModelFirebase.GetAllPostsByUserListener listener){
        modelFirebase.getAllPostsByUser(user,listener);
    }

    /**
     * Authentication
     **/

    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }

    public void createNewAccount(Context context,String fullName,String nickName, String email, String password, String photo,List<String> postList, CreateNewAccount listener) {
        modelFirebase.createNewAccount(context,fullName,nickName, email, password, photo,postList, listener);
    }

    public void signOut(SignOut listener) {
        modelFirebase.signOut(listener);
    }

    public void isExist(Context context, String email, String password, IsExist listener) {
        modelFirebase.isExist(context, email, password, listener);
    }

    public interface AddPhotoToPost {
        void onComplete();
    }
    public void addPhotoToPost(Post post,AddPhotoToPost listener )
    {
        modelFirebase.addPhotoToPost(post,listener);
    }

    public interface AddPhotoToUser {
        void onComplete();
    }

    public void addPhotoToUser(String id, String url,AddPhotoToUser listener )
    {
        modelFirebase.addPhotoToUser(id,url,listener);
    }

    public void isNickNameExist(Context context,String nickName, IsNickNameExist listener)
    {
       modelFirebase.isNickNameExist(context,nickName,listener);
    }
}
