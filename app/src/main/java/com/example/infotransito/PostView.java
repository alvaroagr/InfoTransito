package com.example.infotransito;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PostView extends RecyclerView.ViewHolder {

    private ConstraintLayout root;
    private ImageView img;
    private TextView title;
    private TextView content;

    public PostView(ConstraintLayout root) {
        super(root);
        this.root = root;
        img = root.findViewById(R.id.post_image);
        title = root.findViewById(R.id.post_title);
        content = root.findViewById(R.id.post_content);
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
}
