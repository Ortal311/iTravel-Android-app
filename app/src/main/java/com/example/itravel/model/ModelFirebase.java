package com.example.itravel.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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


    public void getUserById(String Id, Model.GetUserById listener) {
        db.collection(User.collectionName)
                .document(Id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = new User();
                        if (task.isSuccessful()) {
                            Log.d("TAG", "Successfullllllll");
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()) {
                                user = User.create(task.getResult().getData());
                                Log.d("TAG", "exsittttt");
                            }
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

    public interface GetAllPostsByUserListener{
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

    public void getAllPostsByUser(User user, GetAllPostsByUserListener listener) {
        db.collection(Post.collectionName)
                .whereEqualTo("userName", user.getName())
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

    /**
     * Authentication
     */


    public boolean isSignedIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

//    private void updateUI(FirebaseUser user) {
//
//    }

    public void createNewAccount(String name, String email, String password, String photo, Model.CreateNewAccount listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
//                             Sign in success, update UI with the signed-in user's information
                            User user = new User(name, email, photo);

                            FirebaseFirestore.getInstance()
                                    .collection(User.collectionName)
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Log.d("TAG", "createUserWithEmail:success");
                                        listener.onComplete();
                                    } else {
                                        Log.w("TAG", "-- createUserWithEmail:failure", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.d("TAG", "createUserWithEmail:failure");
                        }
                    }
                });
    }

    public void isExist(String email, String password, Model.IsExist listener){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            listener.onComplete();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                    }


                });
    }

    public void signOut(Model.SignOut listener) {
        FirebaseAuth.getInstance().signOut();
        listener.onComplete();
    }
}
