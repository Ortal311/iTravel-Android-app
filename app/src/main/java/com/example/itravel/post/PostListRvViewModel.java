package com.example.itravel.post;

import android.util.Log;

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

    public LiveData<List<Post>> getDataByUser(String name ) {
        MutableLiveData<List<Post>> lst = new MutableLiveData<>();
List<Post> tmp = new LinkedList<>();
        for (Post post: data.getValue()  ) {
            if(post.getUserName().equals(name)) {
                Log.d("TAG","in++");
                tmp.add(post);
//                lst.getValue().add(post);
            }
        }
        lst.postValue(tmp);
        return lst;
    }
}
