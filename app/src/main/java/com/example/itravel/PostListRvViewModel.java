package com.example.itravel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;

import java.util.List;

public class PostListRvViewModel extends ViewModel {

    LiveData<List<Post>> data;

    public PostListRvViewModel(){
        data = Model.instance.getAll();
    }
    public LiveData<List<Post>> getData() {
        return data;
    }

}
