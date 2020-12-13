package com.example.infotransito;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PostView extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ConstraintLayout root;
    private ImageView img;
    private TextView title;
    private TextView content;
    private OnPostClicked listener;
    private Post post;

    public PostView(ConstraintLayout root) {
        super(root);
        this.root = root;
        img = root.findViewById(R.id.post_image);
        title = root.findViewById(R.id.post_title);
        content = root.findViewById(R.id.post_content);
        root.setOnClickListener(this);
    }

    public ImageView getImg() {
        return img;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getContent() {
        return content;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) listener.OnPostClicked(this.post, this.root);
    }

    public void setListener(OnPostClicked listener) {
        this.listener = listener;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public interface OnPostClicked {
        void OnPostClicked(Post post, View v);
    }
}
