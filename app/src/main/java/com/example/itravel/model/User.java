package com.example.itravel.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class User {
    String fullName="";
    String nickName="";//must be unique
    String email="";
    String photo="";
    List<String> postList =  new LinkedList<>();;
    //photo ...
    final public static String collectionName= "users";

    public User(){

    }

    public User(String fullName,String nickName, String email, String photo, List<String> postList) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.email = email;
        this.photo = photo;
        this.postList = postList;

    }

    public static User create(Map<String, Object> json) {
        String fullName = (String) json.get("fullName");
        String nickName = (String) json.get("nickName");
        String email = (String) json.get("email");
        String photo = (String) json.get("photo");
        List<String> postList = (List<String>) json.get("postList");
        User user = new User(fullName,nickName, email, photo,postList);
        return user;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String name) {
        this.fullName = name;
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
        json.put("fullName", fullName );
        json.put("email", email );
        json.put("photo", photo );
        json.put("postList", postList);
        json.put("nickName", nickName);
        return json;
    }

    public void addNewPost(String postId) {
        this.postList.add(postId);
    }
}

