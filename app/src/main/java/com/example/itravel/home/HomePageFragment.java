package com.example.itravel.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.itravel.R;
import com.example.itravel.login.LoginActivity;
import com.example.itravel.model.AppLocalDb;
import com.example.itravel.model.Model;
import com.example.itravel.model.Post;
import com.example.itravel.post.PostListRvViewModel;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePageFragment extends Fragment {

    PostListRvViewModel viewModel;
    MyAdapter adapter;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;

    public Executor executor = Executors.newFixedThreadPool(1);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(PostListRvViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page,container,false);
        progressBar = view.findViewById(R.id.postlist_progressbar);
        progressBar.setVisibility(View.VISIBLE);
//        Model.instance.deleteAllPostsDao();
        swipeRefresh = view.findViewById(R.id.postlist_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> Model.instance.refreshPostList());

        RecyclerView list = view.findViewById(R.id.postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v,int position) {
                String postId = viewModel.getData().getValue().get(position).getId();
                Navigation.findNavController(v).navigate(HomePageFragmentDirections.actionHomePageFragmentToPostDetailsFragment(postId));
            }
        });

        viewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
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

    private void refresh() {
        progressBar.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleTv;
        TextView locationTv;
        TextView userName;
        ImageView img;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.listrow_title_tv);
            locationTv = itemView.findViewById(R.id.listrow_location_tv);
            userName = itemView.findViewById(R.id.listrow_username_tv);
            img = itemView.findViewById(R.id.listrow_post_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    listener.onItemClick(v,pos);
                }
            });
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Post post = viewModel.getData().getValue().get(position);
            holder.titleTv.setText(post.getTitle());
            holder.locationTv.setText(post.getLocation());
            holder.userName.setText(post.getUserName());
            if(!post.getPhoto().contentEquals("")) {
                Picasso.get()
                        .load(post.getPhoto())
                        .into(holder.img);
            }
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