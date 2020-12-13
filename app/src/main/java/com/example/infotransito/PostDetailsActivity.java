package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PostDetailsActivity extends AppCompatActivity {

    private Post post;
    private TextView title;
    private TextView date;
    private TextView content;
    private EditText commentEdt;
    private Button commentBtn;
    private Button back;
    private RecyclerView commentsList;
    private LinearLayoutManager manager;
    private CommentsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        post = (Post) getIntent().getExtras().get("post");

        title = findViewById(R.id.postTitleTv);
        date = findViewById(R.id.postDateTv);
        content = findViewById(R.id.postContentTv);
        commentBtn = findViewById(R.id.commentBtn);
        commentEdt = findViewById(R.id.commentInput);
        commentsList = findViewById(R.id.commentsListRv);
        back = findViewById(R.id.postBackBtn);

        commentsList.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        adapter = new CommentsAdapter();
        commentsList.setAdapter(adapter);
        commentsList.setLayoutManager(manager);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(post.getTimestamp()));
        title.setText(post.getTitle());
        date.setText(dateString + " - Publicado por " + post.getUserName());
        content.setText(post.getContent());

        commentBtn.setOnClickListener(this::postComment);
        back.setOnClickListener(this::goBack);

        db.collection("users").document(auth.getUid()).get().addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        user = task.getResult().toObject(User.class);
                    } else {
                        Toast.makeText(this, "Hubo un problema por su usuario.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        updateComments();
    }

    public void updateComments() {
        db.collection("comments").whereEqualTo("postId", post.getId()).get().addOnCompleteListener(
                task -> {
                    for(DocumentSnapshot document : task.getResult().getDocuments()) {
                        Comment currentComment = document.toObject(Comment.class);
                        adapter.addComment(currentComment);
                    }
                }
        );
    }

    private void postComment(View v) {
        if(!commentEdt.getText().toString().trim().isEmpty()) {
//            Comment comment = new Comment(UUID.randomUUID().toString(), commentEdt.getText().toString(), post.getUserId(), post.getUserName(), post.getId(), System.currentTimeMillis());
            Comment comment = new Comment(UUID.randomUUID().toString(), commentEdt.getText().toString(), user.getId(), user.getName(), post.getId(), System.currentTimeMillis());
            db.collection("comments").document(comment.getId()).set(comment);
            adapter.addComment(comment);
            commentEdt.setText("");
            runOnUiThread(
                    () -> {
                        Toast.makeText(this, "Tu comentario fue publicado", Toast.LENGTH_SHORT).show();
                    }
            );
        } else {
            runOnUiThread(
                    () -> {
                        Toast.makeText(this, "Necesitas escribir algo en tu comentario", Toast.LENGTH_SHORT).show();
                    }
            );
        }
    }

    private void goBack(View v) {
        finish();
    }
}