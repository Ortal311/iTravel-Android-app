package com.example.itravel;

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
import android.widget.TextView;

import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.model.User;
import com.example.itravel.post.PostDetailsFragmentArgs;
import com.example.itravel.post.PostListRvViewModel;
import com.google.firebase.auth.FirebaseAuth;


public class ProfilePageFragment extends Fragment {

    TextView nameTv;
    ImageView image;
    ImageButton editBtn;
    ImageButton addPostBtn;

    PostListRvViewModel viewModel;
    MyAdapterProfile adapter;
    SwipeRefreshLayout swipeRefresh;

    User currUser;

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
        image = view.findViewById(R.id.profile_image);
        editBtn = view.findViewById(R.id.profile_editProfile_btn);
        addPostBtn = view.findViewById(R.id.profile_addPost_btn);

//        String name = ProfilePageFragmentArgs.fromBundle(getArguments()).getName();

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToPostAddFragment());
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToProfileEdit(nameTv.getText().toString()));
            }
        });

        getUserDetails();

        swipeRefresh = view.findViewById(R.id.profilePage_postlist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostListByUser(currUser));

        RecyclerView list = view.findViewById(R.id.profilePage_postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapterProfile();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListenerProfile() {
            @Override
            public void onItemClick(View v,int position) {
                String postTitle = viewModel.getData().getValue().get(position).getTitle();
                // Navigation.findNavController(v).navigate(StudentListRvFragmentDirections.actionStudentListRvFragmentToStudentDetailsFragment(stId));
            }
        });

        viewModel.getData().observe(getViewLifecycleOwner(), posts -> refresh());
        swipeRefresh.setRefreshing(Model.instance.getPostListLoadingState().getValue() == Model.PostListLoadingState.loading);

        Model.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if (postListLoadingState == Model.PostListLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            } else{
                swipeRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    public void getUserDetails() {
        String ID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Model.instance.getUserById(ID, new Model.GetUserById() {
            @Override
            public void onComplete(User user) {
                Log.d("TAG", "111");
                currUser = user;
                nameTv.setText(user.getName());
            }
        });
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    class MyViewHolderProfile extends RecyclerView.ViewHolder{
        TextView titleTv;
        TextView locationTv;

        public MyViewHolderProfile(@NonNull View itemView, OnItemClickListenerProfile listener) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.listrow_title_tv);
            locationTv = itemView.findViewById(R.id.listrow_location_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v,pos);
                }
            });
        }
    }

    interface OnItemClickListenerProfile{
        void onItemClick(View v,int position);
    }

    class MyAdapterProfile extends RecyclerView.Adapter<MyViewHolderProfile>{
        OnItemClickListenerProfile listener;
        public void setOnItemClickListener(OnItemClickListenerProfile listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolderProfile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row,parent,false);
            MyViewHolderProfile holder = new MyViewHolderProfile(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolderProfile holder, int position) {
            Post post = viewModel.getData().getValue().get(position);
            holder.titleTv.setText(post.getTitle());
            holder.locationTv.setText(post.getLocation());
        }

        @Override
        public int getItemCount() {
            if(viewModel.getData().getValue() == null){
                return 0;
            }
            return viewModel.getData().getValue().size();
        }

    }
}