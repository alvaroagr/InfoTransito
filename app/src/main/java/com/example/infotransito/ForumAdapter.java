package com.example.infotransito;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ForumAdapter extends RecyclerView.Adapter<PostView> implements PostView.OnPostClicked {

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
        holder.setPost(currentPost);
        holder.getTitle().setText(currentPost.getTitle());
        holder.getContent().setText(currentPost.getContent());
        holder.setListener(this);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void OnPostClicked(Post post, View v) {
        Intent i = new Intent(v.getContext(), PostDetails.class);
        i.putExtra("post", post);
        v.getContext().startActivity(i);
    }
}
