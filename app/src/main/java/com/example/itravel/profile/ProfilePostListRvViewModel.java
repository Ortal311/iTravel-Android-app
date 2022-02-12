package com.example.itravel.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//TODO: CHECK IF NEED TO DELETE!!!!

public class ProfilePostListRvViewModel extends ViewModel {

    LiveData<List<Post>> data;

    public ProfilePostListRvViewModel(){
        data = Model.instance.getAllByUser();
        if(data.getValue()!=null)
            Collections.reverse(data.getValue());
    }

//    public PostListRvViewModel(User user){
//        data = Model.instance.getAllPostsByUser(user);
//    }

    public LiveData<List<Post>> getData() {
        return data;
    }

    public LiveData<List<Post>> getDataByUser(String nickName ) {
        MutableLiveData<List<Post>> lst = new MutableLiveData<>();
        List<Post> tmp = new LinkedList<>();
        for (Post post: data.getValue()  ) {
            if(post.getUserName().equals(nickName)) {
                Log.d("TAG","in++");
                tmp.add(post);
//                lst.getValue().add(post);
            }
        }

        lst.postValue(tmp);
        return lst;
    }
}
