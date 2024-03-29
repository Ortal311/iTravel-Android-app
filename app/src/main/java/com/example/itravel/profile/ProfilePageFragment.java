package com.example.itravel.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.itravel.R;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.example.itravel.post.PostListRvViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ProfilePageFragment extends Fragment {

    TextView nameTv;
    TextView nickNameTv;
    ImageView image;
    ImageButton editBtn;
    ImageButton addPostBtn;

    RecyclerView list;
    MyAdapterProfile adapter;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    User currUser;
    PostListRvViewModel viewModel;
    String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostListRvViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        nameTv = view.findViewById(R.id.profile_name_tv);
        nickNameTv = view.findViewById(R.id.profile_nickName_tv);
        image = view.findViewById(R.id.profile_image);
        editBtn = view.findViewById(R.id.profile_editProfile_btn);
        addPostBtn = view.findViewById(R.id.profile_addPost_btn);
        progressBar = view.findViewById(R.id.profilepage_progressBar);

        addPostBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToPostAddFragment()));
        editBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToProfileEdit(nameTv.getText().toString())));
        progressBar.setVisibility(View.VISIBLE);
        User usr = getUserDetails();

        list = view.findViewById(R.id.profilePage_postlist_rv);
        list.setHasFixedSize(true);
        Log.d("TAG", getContext().toString());
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        updateUI(View.INVISIBLE);

        swipeRefresh = view.findViewById(R.id.profilePage_postlist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            Model.instance.refreshPostListByUser(currUser.getNickName());//return the right number of posts
        });

        adapter = new MyAdapterProfile();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((v, position) -> {
            String postId = viewModel.getData().getValue().get(position).getId();
            Navigation.findNavController(v).navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToPostDetailsFragment(postId));
        });

        viewModel.getDataByUser().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getUserPostListLoadingState().getValue() == Model.UserPostListLoadingState.loading);

        Model.instance.getUserPostListLoadingState().observe(getViewLifecycleOwner(), userPostListLoadingState -> {
            if (userPostListLoadingState == Model.UserPostListLoadingState.loading) {
                swipeRefresh.setRefreshing(true);
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    public User getUserDetails() {
        Model.instance.getUserById(ID, user -> {
            currUser = user;
            nameTv.setText(user.getFullName());
            nickNameTv.setText(user.getNickName());
            if (currUser.getPhoto() != "") {
                Picasso.get()
                        .load(currUser.getPhoto())
                        .into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                                updateUI(View.VISIBLE);
                            }
                            @Override
                            public void onError(Exception e) {
                            }
                        });
            }
            progressBar.setVisibility(View.GONE);
            updateUI(View.VISIBLE);
            Model.instance.refreshPostListByUser(currUser.getNickName());
        });

        return currUser;
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolderProfile extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView locationTv;
        TextView userName;
        ImageView img;

        public MyViewHolderProfile(@NonNull View itemView, OnItemClickListenerProfile listener) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.listrow_title_tv);
            locationTv = itemView.findViewById(R.id.listrow_location_tv);
            userName = itemView.findViewById(R.id.listrow_username_tv);
            img = itemView.findViewById(R.id.listrow_post_img);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                listener.onItemClick(v, pos);
            });
        }
    }

    interface OnItemClickListenerProfile {
        void onItemClick(View v, int position);
    }

    class MyAdapterProfile extends RecyclerView.Adapter<MyViewHolderProfile> {
        OnItemClickListenerProfile listener;

        public void setOnItemClickListener(OnItemClickListenerProfile listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolderProfile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row, parent, false);
            MyViewHolderProfile holder = new MyViewHolderProfile(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolderProfile holder, int position) {
            Post post = viewModel.getDataByUser().getValue().get(position);
            holder.titleTv.setText(post.getTitle());
            holder.locationTv.setText(post.getLocation());
            holder.userName.setText(post.getUserName());
            if (!post.getPhoto().contentEquals("")) {
                Picasso.get()
                        .load(post.getPhoto())
                        .into(holder.img);
            }
        }

        @Override
        public int getItemCount() {
            if (viewModel.getDataByUser().getValue() == null) {
                return 0;
            }
            return viewModel.getDataByUser().getValue().size();
        }
    }

    public void updateUI(int type) {
        image.setVisibility(type);
        nameTv.setVisibility(type);
        nickNameTv.setVisibility(type);
        editBtn.setVisibility(type);
        addPostBtn.setVisibility(type);
        list.setVisibility(type);
    }
}