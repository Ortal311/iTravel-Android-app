package com.example.itravel.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class User {
    String name="";
    String email="";
    String photo="";
    List<String> postList =  new LinkedList<>();;
    //photo ...
    final public static String collectionName= "users";

    public User(){

    }

    public User(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;

    }

    public static User create(Map<String, Object> json) {
        String name = (String) json.get("name");
        String email = (String) json.get("email");
        String photo = (String) json.get("photo");
//        List<String> postList = (List<String>) json.get("postList");
        User user = new User(name, email, photo);
//        user.addNewPost(postList.get(postList.size()-1));

        return user;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getPostList() {
        return postList;
    }

    public void setPostList(List<String> postList) {
        this.postList = postList;
    }


    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", name );
        json.put("email", email );
        json.put("photo", photo );
        json.put("postList", postList);
        return json;
    }

    public void addNewPost(String postId) {
        this.postList.add(postId);
    }
}

