package com.example.itravel.model;

import java.util.LinkedList;
import java.util.List;

public class Model {
    public static final Model instance = new Model();

    private Model(){
        for(int i=0;i<data.size();i++){
            User u = new User("name", "" + i, "phone", "address");
            data.add(u);
        }
    }

    List<User> data = new LinkedList<User>();

    public List<User> getAllUsers(){
        return data;
    }

    public void addUser(User user){
        data.add(user);
    }

    public User getUserById(String userId) {
        for (User u:data
             ) {
            if (u.getId().equals(userId)){
                return u;
            }
        }
        return null;
    }
}
