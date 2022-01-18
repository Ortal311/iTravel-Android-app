package com.example.itravel.post;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.itravel.MainActivity;
import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.google.firebase.auth.FirebaseAuth;

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
    String difficulty;


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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, itemsDropdown);
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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView spinner_item_text = (TextView) view;
                difficulty = spinner_item_text.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                difficulty="";
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

        String Id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Model.instance.getUserById(Id, new Model.GetUserByEmail() {
            @Override
            public void onComplete(User user) {
                String name = user.getName();
                savePost(name, title, description, location, difficulty);
            }
        });
    }

    public void savePost(String name, String title, String description, String location, String difficulty) {
        Post post = new Post(name, title, description, location, difficulty);
        Model.instance.addPost(post,()->{
            Model.instance.refreshPostList();
            Navigation.findNavController(view).navigate(PostAddFragmentDirections.actionPostAddFragmentToHomePageFragment2());

        });
    }

}