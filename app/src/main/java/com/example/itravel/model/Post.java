package com.example.itravel.model;

import java.util.HashMap;
import java.util.Map;

public class Post {
    final public static String collectionName = "posts";

    String userName;  // Find the connected user
    String title = "";
    String description = "";
    String location = "";
    //difficulty
    //pictures

    public Post(){}

    public Post(String userName, String title, String description, String location) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.location = location;
    }


    public static Post create(Map<String, Object> json) {
        String userName = (String) json.get("userName");
        String title = (String) json.get("title");
        String description = (String) json.get("description");
        String location = (String) json.get("location");
        Post post = new Post(userName, title, description, location);
        return post;
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

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("userName", userName);
        json.put("title", title );
        json.put("description", description );
        json.put("location", location );
        return json;
    }
}
