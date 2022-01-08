package com.example.itravel.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addUser(User user, Model.AddUserListener listener){

        // Create a new user with a first and last name
        Map<String, Object> json = user.toJson();

        // Add a new document with a generated ID
        db.collection(User.collectionName).document(user.getEmail())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void updateUser(User user, Model.UpdateUserListener listener) {
        db.collection(User.collectionName).document(user.getEmail())
                .update("postList", true)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public interface GetAllUsersListener{
        void onComplete(List<User> list);
    }

    public void getAllUsers(GetAllUsersListener listener) {
        db.collection(User.collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> list = new LinkedList<User>();
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                User user = User.create(doc.getData());
                                if (user != null)
                                    list.add(user);
                            }
                        }
                        listener.onComplete(list);
                    }

                });
    }
    public void getUserByEmail(String userEmail, Model.GetUserByEmail listener) {
        DocumentReference dr = db.collection(User.collectionName).document(userEmail);
        Task<DocumentSnapshot> task = dr.get();

//        if(task.isSuccessful()){
//            DocumentSnapshot r = task.getResult();
//            Map<String, Object> m = r.getData();
//            User user = User.create(task.getResult().getData());
//            listener.onComplete(user);
//            Log.d("TAG", "hrfurhurwhwufhrufhwfwohwuhfuohwohfe");
//        }
        dr.get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = new User();
                if ((task.isSuccessful()) & (task.getResult().getData() != null)) {
                    user = User.create(task.getResult().getData());
                }
                listener.onComplete(user);
            }
        });
    }

    public void addPost(Post post, Model.AddPostListener listener){

        // Create a new user with a first and last name
        Map<String, Object> json = post.toJson();

        // Add a new document with a generated ID
        db.collection(Post.collectionName)
                .document(post.getTitle())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public interface GetAllPostsListener{
        void onComplete(List<Post> list);
    }

    public void getAllPosts(GetAllPostsListener listener) {
        db.collection(Post.collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Post> list = new LinkedList<Post>();
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Post post = Post.create(doc.getData());
                                if (post != null)
                                    list.add(post);
                            }
                        }
                        listener.onComplete(list);
                    }

                });
    }

    public void getAllPostsByUser(User user, GetAllPostsListener listener) {
        db.collection(Post.collectionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Post> list = new LinkedList<Post>();
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Post post = Post.create(doc.getData());
                                if (post != null && post.userName == user.getName())
                                    list.add(post);
                            }
                        }
                        listener.onComplete(list);
                    }
                });
    }

    public void getPostByTitle(String postTitle, Model.GetPostByTitle listener) {
        db.collection(Post.collectionName)
                .document(postTitle)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if ((task.isSuccessful()) & (task.getResult().getData() != null)) {
                            post = Post.create(task.getResult().getData());
                        }
                        listener.onComplete(post);
                    }
                });
    }

}
