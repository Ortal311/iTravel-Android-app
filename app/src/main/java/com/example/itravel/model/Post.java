package com.example.itravel.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Post {
    final public static String collectionName = "posts";
    @PrimaryKey
    @NonNull
    String id="";
    String title = "";
    String userName;  // Find the connected user
    String description = "";
    String location = "";
    String difficulty="";
    Long updateDate = new Long(0);
    String photo="";

    public Post(){}

    public Post( String id,String userName, String title, String description,String photo, String location, String difficulty) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.difficulty = difficulty;
        this.id = id;
        this.photo = photo;
    }

    public static Post create(String id, Map<String, Object> json) {
        String userName = (String) json.get("userName");
        String title = (String) json.get("title");
        String description = (String) json.get("description");
        String location = (String) json.get("location");
        String difficulty = (String) json.get("difficulty");
        Timestamp ts = (Timestamp)json.get("updateDate");
        String photo = (String) json.get("photo");
        Long updateDate = ts.getSeconds();

        Post post = new Post(id, userName, title, description,photo, location, difficulty);
        post.setUpdateDate(updateDate);
        return post;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("userName", userName);
        json.put("title", title );
        json.put("description", description );
        json.put("location", location );
        json.put("difficulty", difficulty);
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("photo",photo);
        return json;
    }
}
