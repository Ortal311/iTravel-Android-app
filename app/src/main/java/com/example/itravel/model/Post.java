package com.example.itravel.model;

import android.widget.Button;
import android.widget.Spinner;

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
    String title = "";
    String userName;  // Find the connected user
    String description = "";
    String location = "";
    String difficulty="";
    Long updateDate = new Long(0);

//pictures

    public Post(){}

    public Post(String userName, String title, String description, String location, String difficulty) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.difficulty = difficulty;
    }

    public static Post create(Map<String, Object> json) {
        String userName = (String) json.get("userName");
        String title = (String) json.get("title");
        String description = (String) json.get("description");
        String location = (String) json.get("location");
        String difficulty = (String) json.get("difficulty");
        Timestamp ts = (Timestamp)json.get("updateDate");
        Long updateDate = ts.getSeconds();

        Post post = new Post(userName, title, description, location, difficulty);
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

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("userName", userName);
        json.put("title", title );
        json.put("description", description );
        json.put("location", location );
        json.put("difficulty", difficulty);
        json.put("updateDate", FieldValue.serverTimestamp());


        return json;
    }

}
