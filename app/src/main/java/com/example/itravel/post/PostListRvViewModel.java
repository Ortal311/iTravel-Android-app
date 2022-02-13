package com.example.itravel.post;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.itravel.model.Model;
import com.example.itravel.model.ModelFirebase;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;

import java.util.Collections;
import java.util.LinkedList;
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

//    public LiveData<List<Post>> getDataByUser(String nickName ) {
//        MutableLiveData<List<Post>> lst = new MutableLiveData<>();
//        List<Post> tmp = new LinkedList<>();
//        for (Post post: data.getValue()  ) {
//            if(post.getUserName().equals(nickName)) {
//                Log.d("TAG","in++");
//                tmp.add(post);
////                lst.getValue().add(post);
//            }
//        }
//
//        lst.postValue(tmp);
//        return lst;
//    }
}
