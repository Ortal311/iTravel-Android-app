package com.example.itravel.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    String name="";
    String email="";
    String password="";
    //photo ...
    final public static String collectionName= "users";

    public User(){

    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static User create(Map<String, Object> json) {
        String name = (String) json.get("name");
        String email = (String) json.get("email");
        String password = (String) json.get("password");
        User user = new User(name, email, password);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<>();
        json.put("name", name );
        json.put("email", email );
        json.put("password", password );
        return json;
    }
}

