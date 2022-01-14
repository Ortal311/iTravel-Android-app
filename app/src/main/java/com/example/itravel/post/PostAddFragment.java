package com.example.itravel.post;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;

public class PostAddFragment extends Fragment {
    EditText titleEt;
    EditText descriptionEt;
    EditText locationEt;
    //difficulty-ADD
    //Picture-ADD
    Button saveBtn;
    Button cancelBtn;
    View view;

    String userEmail;
    String userPassword;

    Spinner dropdown;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences sp1 = context.getSharedPreferences("Login", 0);
        userEmail = sp1.getString("email", null);
        userPassword = sp1.getString("password", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_post_add, container, false);
        titleEt = view.findViewById(R.id.addpost_title_show);
        descriptionEt = view.findViewById(R.id.addpost_description_show);
        locationEt = view.findViewById(R.id.addpost_location_show);
        saveBtn=view.findViewById(R.id.addpost_save_btn);
        cancelBtn=view.findViewById(R.id.addpost_cancel_btn);
        dropdown = view.findViewById(R.id.addpost_dropdown);
        String[] itemsDropdown = new String[]{"easy", "medium", "hard"};
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemsDropdown);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                //navigate to home page
//                Navigation.findNavController(v).navigate(PostAddFragmentDirections.actionPostAddFragmentToHomePageFragment2());
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        return view;
    }

    private void save(){
        saveBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        String title = titleEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String location = locationEt.getText().toString();
        User user = new User();
        Model.instance.getUserByEmail(userEmail, new Model.GetUserByEmail() {
            @Override
            public void onComplete(User u) {
                user.setEmail(u.getEmail());
                user.setName(u.getName());
            }
        });

        Log.d("TAG", "User: " + user.getEmail());

        Post post = new Post(user.getName(), title, description, location);
        user.addNewPost(post);

        Model.instance.addPost(post,()->{
            Navigation.findNavController(view).navigate(PostAddFragmentDirections.actionPostAddFragmentToHomePageFragment2());
        });
    }

}