package com.example.infotransito;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumAdapter extends RecyclerView.Adapter<PostView> {

    private ArrayList<Post> posts;

    public ForumAdapter() {
        posts = new ArrayList<>();
    }

    public void addPost(Post post) {
        posts.add(post);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.post_row, parent, false);
        ConstraintLayout rowroot = (ConstraintLayout) row;
        PostView postView = new PostView(rowroot);
        return postView;
    }

    @Override
    public void onBindViewHolder(@NonNull PostView holder, int position) {
        Post currentPost = posts.get(position);
        holder.getTitle().setText(currentPost.getTitle());
        holder.getContent().setText(currentPost.getContent());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
