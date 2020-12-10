package com.example.infotransito;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentView> {

    private ArrayList<Comment> comments;

    public CommentsAdapter() {
        comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }

    public void clearComments() {
        comments.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row = inflater.inflate(R.layout.comment_row, parent, false);
        ConstraintLayout rowroot = (ConstraintLayout) row;
        CommentView commentView = new CommentView(rowroot);
        return commentView;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentView holder, int position) {
        Comment comment = comments.get(position);
        holder.getTitle().setText(comment.getUserName());
        holder.getContent().setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
