package com.example.itravel.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.itravel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ModelFirebase {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Post
     **/

    public interface GetAllPostsListener {
        void onComplete(List<Post> list);
    }

    public void addPost(Post post, User user, Model.AddPostListener listener) {
        // Create a new user with a first and last name
        Map<String, Object> json = post.toJson();
        // Add a new document with a generated ID
        Task<DocumentReference> ref = db.collection(Post.collectionName)
                .add(json)
                .addOnCompleteListener(task -> {
                    String id = task.getResult().getId();
                    addPostToUsersList(id, user);
                    listener.onComplete(id);
                });
    }

    public void addPhotoToPost(Post post, Model.AddPhotoToPost listener) {
        db.collection(Post.collectionName)
                .document(post.getId())
                .update("photo", post.getPhoto()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "lplplp");
            }
        });
    }

    public void addPostToUsersList(String postId, User user) {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(User.collectionName)
                .document(id)
                .update("postList", FieldValue.arrayUnion(postId))
                .addOnSuccessListener(unused -> user.addNewPost(postId));
    }

    public void removePostToUsersList(String postId) {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(User.collectionName)
                .document(id)
                .update("postList", FieldValue.arrayRemove(postId))
                .addOnSuccessListener(unused -> {
                });
    }


    public void updatePost(Post post, Model.UpdatePostListener listener) {

        db.collection(Post.collectionName).document(post.getId())
                .update("description", post.getDescription(),
                        "location", post.getLocation(),
                        "difficulty", post.getDifficulty(),
                        "title", post.getTitle(),
                        "photo", post.getPhoto(),
                        "userName", post.getUserName()

                )
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());

    }

    public void deletePost(Post post, Model.deletePost listener) {
        db.collection(Post.collectionName).document(post.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    removePostToUsersList(post.getId());
                    listener.onComplete();
                })
                .addOnFailureListener(e -> Log.w("TAG", "Error deleting document", e));
    }

    public void getAllPosts(Long lastUpdateDate, GetAllPostsListener listener) {
        db.collection(Post.collectionName)
                .whereGreaterThanOrEqualTo("updateDate", new Timestamp(lastUpdateDate, 0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getReference().getId();
                            Post post = Post.create(id, doc.getData());
                            if (post != null) {
                                list.add(post);
                            }
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getPostById(String postId, Model.GetPostById listener) {
        db.collection(Post.collectionName)
                .document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    Post post = new Post();
                    if (task.isSuccessful()) {
                        DocumentSnapshot docSnapshot = task.getResult();
                        if (docSnapshot.exists()) {
                            post = Post.create(postId, task.getResult().getData());
                        }
                    }
                    listener.onComplete(post);
                });
    }

    /**
     * User
     **/

    public interface GetAllPostsByUserListener {
        void onComplete(List<Post> list);
    }
    public void updateUser(String id, String name, String nickName, String photo, Model.UpdateUserListener listener) {
        db.collection(User.collectionName)
                .document(id)
                .update("fullName", name,
                        "nickName", nickName
                        , "photo", photo)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void addPhotoToUser(String id, String url, Model.AddPhotoToUser listener) {
        db.collection(Post.collectionName)
                .document(id)
                .update("photo", url).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "lplplp");
            }
        });
    }

    public void getUserById(String Id, Model.GetUserById listener) {
        db.collection(User.collectionName)
                .document(Id)
                .get()
                .addOnCompleteListener(task -> {
                    User user = new User();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = user.create(task.getResult().getData());
                        }
                    }
                    listener.onComplete(user);
                });
    }

    public void isNickNameExist(Context context, String nickName, Model.IsNickNameExist listener) {

        Task<QuerySnapshot> res = db.collection(User.collectionName)
                .whereEqualTo("nickName", nickName)
                .get().addOnCompleteListener(task -> {
                    if (task.getResult().size() == 0) {
                        listener.onComplete();
                    } else {
                        listener.onFail();
                    }
                });
    }

    public void getAllPostsByUser(User user, GetAllPostsByUserListener listener) {
        db.collection(Post.collectionName)
                .whereEqualTo("userName", user.getNickName())
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Post post = Post.create(doc.getId(),doc.getData());
                            list.add(post);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    /**
     * Authentication
     **/
    public boolean isSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }

    public void createNewAccount(Context context, String fullName, String nickName, String email, String password, String photo, List<String> postList, Model.CreateNewAccount listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        User user = new User(fullName, nickName, email, photo, postList);

                        FirebaseFirestore.getInstance()
                                .collection(User.collectionName)
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d("TAG", "createUserWithEmail:success");
                                listener.onComplete();
                            } else {
                                Log.w("TAG", "-- createUserWithEmail:failure", task1.getException());
                            }
                        });
                    } else {
                        Log.d("TAG", "createUserWithEmail:failure");
                        Toast toast = new Toast(context);
                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);
                        TextView toastText = popupView.findViewById(R.id.popup_text_tv);
                        toastText.setText("Email Already taken!");
                        toastText.setTextSize(20);
                        toast.setView(popupView);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
    }

    public void isExist(Context context, String email, String password, Model.IsExist listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        listener.onComplete();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                        Toast toast = new Toast(context);
                        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);
                        TextView toastText = popupView.findViewById(R.id.popup_text_tv);
                        toastText.setText("You need to register first!");
                        toastText.setTextSize(20);
                        toast.setView(popupView);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
    }

    public void signOut(Model.SignOut listener) {
        FirebaseAuth.getInstance().signOut();
        listener.onComplete();
    }

    /**
     * Stroage
     **/
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void saveUserImage(Bitmap imageBitmap, String imageName, Model.SaveImageListener listener) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("/user_avatars/" + imageName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            listener.onComplete(null);
        }).addOnSuccessListener(taskSnapshot -> {
            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                listener.onComplete(downloadUrl.toString());
            }).addOnFailureListener(e -> Log.d("TAG", "error here!"));
        });

    }

    public void savePostImage(Bitmap imageBitmap, String imageName, Model.SaveImageListener listener) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("/post_images/" + imageName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            listener.onComplete(null);
        }).addOnSuccessListener(taskSnapshot -> {
            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Uri downloadUrl = uri;
                listener.onComplete(downloadUrl.toString());
            }).addOnFailureListener(e -> Log.d("TAG", "error here!"));
        });

    }
}
