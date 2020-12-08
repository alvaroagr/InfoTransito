package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private Button backBtn, publishBtn, imgBtn;
    private TextView titleET, contentET;

    private String path;

    private String userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        userId = getIntent().getExtras().getString("userId");
        username = getIntent().getExtras().getString("username");

        backBtn = findViewById(R.id.backBtn);
        publishBtn = findViewById(R.id.publishBtn);
        imgBtn = findViewById(R.id.imgBtn);
        titleET = findViewById(R.id.titleET);
        contentET = findViewById(R.id.contentET);

        backBtn.setOnClickListener(
                (v) -> {
                    exit();
                }
        );

        publishBtn.setOnClickListener(
                (v) -> {
                    String title = titleET.getText().toString();
                    String content = contentET.getText().toString();

                    Post post = new Post(UUID.randomUUID().toString(), title, content, userId, username);
                    post.setTimestamp(new Date().getTime());

                    FirebaseFirestore.getInstance()
                            .collection("posts")
                            .document(post.getId())
                            .set(post)
                            .addOnCompleteListener(
                                    task -> {
                                        if(task.isSuccessful()){
                                            exit();
                                        } else {
                                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                            );
                }
        );

        imgBtn.setOnClickListener(
                (v) -> {
                    Toast.makeText(this, "Add Image", Toast.LENGTH_LONG).show();
                }
        );


    }

    public void exit(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.close);
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}