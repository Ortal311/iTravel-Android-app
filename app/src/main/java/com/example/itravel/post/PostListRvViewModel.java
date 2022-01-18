package com.example.itravel.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;

import java.util.LinkedList;
import java.util.List;

public class PostListRvViewModel extends ViewModel {

    LiveData<List<Post>> data;

    public PostListRvViewModel(){
        data = Model.instance.getAll();
    }

//    public PostListRvViewModel(User user){
//        data = Model.instance.getAllPostsByUser(user);
//    }

    public LiveData<List<Post>> getData() {
        return data;
    }
}
