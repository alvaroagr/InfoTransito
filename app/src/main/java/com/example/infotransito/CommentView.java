package com.example.infotransito;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CommentView extends RecyclerView.ViewHolder {

    private ConstraintLayout root;
    private TextView title;
    private TextView content;

    public CommentView(@NonNull ConstraintLayout root) {
        super(root);
        this.root = root;
        title = root.findViewById(R.id.commentTitle);
        content = root.findViewById(R.id.commnentContent);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getContent() {
        return content;
    }
}
