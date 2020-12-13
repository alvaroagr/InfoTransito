package com.example.infotransito;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private Button backBtn, publishBtn, imgBtn;
    private TextView titleET, contentET;

    private FirebaseStorage storage;
    private String path;
    private static final int GALLERY_CALLBACK = 1;

    private String userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storage = FirebaseStorage.getInstance();

        userId = getIntent().getExtras().getString("userId");
        username = getIntent().getExtras().getString("username");

        backBtn = findViewById(R.id.postBackBtn);
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
                    String photoId = "";
                    if(path != null) {
                        try {
                            photoId = UUID.randomUUID().toString();
                            FileInputStream fis = new FileInputStream(new File(path));
                            storage.getReference().child("post_images").child(photoId).putStream(fis);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e(">>>", e.getMessage());
                        }
                    }

                    Post post = new Post(UUID.randomUUID().toString(), title, content, userId, username, photoId);
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
                    if(checkPermission()) {
                        Intent i = new Intent(Intent.ACTION_PICK);
                        i.setType("image/*");
                        startActivityForResult(i, GALLERY_CALLBACK);
                        Toast.makeText(this, "Add Image", Toast.LENGTH_LONG).show();
                        Log.e(">>>", "Has permission");
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CALLBACK && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            path = UtilDomi.getPath(this, photoUri);
        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Ask for the permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            Toast.makeText(this, "Por favor otorgar permiso de acceso a tu galería", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}