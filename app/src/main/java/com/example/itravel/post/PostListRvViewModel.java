package com.example.itravel.post;

import androidx.lifecycle.LiveData;

import androidx.lifecycle.ViewModel;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;

import java.util.List;

public class PostListRvViewModel extends ViewModel {

    LiveData<List<Post>> data;
    LiveData<List<Post>> usersData;

    public PostListRvViewModel(){
        data = Model.instance.getAll();
        usersData = Model.instance.getAllByUser();

    }

    public LiveData<List<Post>> getData() {
        return data;
    }

    public LiveData<List<Post>> getDataByUser() {
        return usersData;
    }
}
