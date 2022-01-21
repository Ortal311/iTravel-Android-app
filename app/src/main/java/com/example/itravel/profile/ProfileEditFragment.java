package com.example.itravel.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.post.PostDetailsFragmentArgs;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileEditFragment extends Fragment {

    TextView nameEt;
    ImageButton imgBtn;
    Button saveBtn;
    Button cancelBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        nameEt = view.findViewById(R.id.editProfile_name);
        imgBtn = view.findViewById(R.id.editProfile_img_btn);
        saveBtn = view.findViewById(R.id.editProfile_save_btn);
        cancelBtn = view.findViewById(R.id.editProfile_cancel_btn);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = ProfilePageFragmentArgs.fromBundle(getArguments()).getName();

        nameEt.setText(name);

        saveBtn.setOnClickListener(v -> Model.instance.updateUser(id, nameEt.getText().toString(), new Model.UpdateUserListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(v).navigateUp();
            }
        }));
        cancelBtn.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());


        return view;

    }
}