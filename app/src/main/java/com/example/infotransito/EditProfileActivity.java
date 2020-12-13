package com.example.infotransito;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CAMERA_CALLBACK = 101 ;
    private static final int GALLERY_CALLBACK = 102;

    private User oldUser;

    private String path;

    private TextInputLayout nameWrapper, emailWrapper;
    private Button saveBtn;
    private ImageButton addImgBtn;
    //    private ImageView profileImg;
    private ShapeableImageView profileShapeImg;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        oldUser = (User) getIntent().getExtras().getSerializable("user");

        profileShapeImg = findViewById(R.id.profileIV);
        nameWrapper = findViewById(R.id.nameWrapper);
        emailWrapper = findViewById(R.id.emailWrapper);
        saveBtn = findViewById(R.id.saveBtn);
        addImgBtn = findViewById(R.id.addImgBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        store = FirebaseStorage.getInstance();

        nameWrapper.getEditText().setText(oldUser.getName());
        emailWrapper.getEditText().setText(oldUser.getEmail());

        saveBtn.setOnClickListener(this::save);
        addImgBtn.setOnClickListener(this::addImg);

        FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(oldUser.getImg())
                .getDownloadUrl()
                .addOnCompleteListener(
                        task -> {
                            if(task.isSuccessful()){
                                String url = task.getResult().toString();
                                Glide.with(profileShapeImg).load(url).into(profileShapeImg);
                            } else {
                                Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_LONG).show();
                            }
                        }
                );
    }

    private void addImg(View view) {
        Intent j = new Intent(Intent.ACTION_GET_CONTENT);
        j.setType("image/*");
        startActivityForResult(j, GALLERY_CALLBACK);
    }

    private void save(View v){
        String name = nameWrapper.getEditText().getText().toString();
        String email = emailWrapper.getEditText().getText().toString();

        // Validate data
        if(name.trim().isEmpty()){
            nameWrapper.setError("Escribe un nombre");
            return;
        }else nameWrapper.setError(null);

        if(email.trim().isEmpty()){
            emailWrapper.setError("Escribe un correo");
            return;
        } else emailWrapper.setError(null);

        if(!TextUtil.isValidEmailAddress(email)){
            emailWrapper.setError("Escribe un correo válido");
            return;
        } else emailWrapper.setError(null);

        if(!name.trim().equals(oldUser.getName()) ||
                !email.trim().equals(oldUser.getEmail()) ||
                path != null
        ){
            db.collection("users")
                    .document(oldUser.getId())
                    .update("name", name, "email", email).addOnCompleteListener(
                            task -> {
                                if(task.isSuccessful()){
                                    auth.getCurrentUser().updateEmail(email).addOnCompleteListener(
                                            innerTask -> {
                                                if(innerTask.isSuccessful() && path != null){
                                                    try {
                                                        store.getReference().child("profile_images")
                                                                .child(oldUser.getImg())
                                                                .putStream(new FileInputStream(new File(path)))
                                                                .addOnCompleteListener(
                                                                        finalTask -> {
                                                                            if(finalTask.isSuccessful()){
                                                                                finish();
                                                                            }
                                                                        });
                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    finish();
                                                }
                                            }
                                    );
                                }
                            });
        }



//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
//                createTask -> {
//                    if(createTask.isSuccessful()){
//                        if(path != null) {
//                            try {
//                                String img = UUID.randomUUID().toString();
//                                FirebaseStorage.getInstance()
//                                        .getReference()
//                                        .child("profile_images")
//                                        .child(img)
//                                        .putStream(new FileInputStream(new File(path)))
//                                        .addOnCompleteListener(
//                                                task -> {
//                                                    if(task.isSuccessful()){
//                                                        String uid = auth.getCurrentUser().getUid();
//                                                        HashMap<String, String> user = new HashMap<>();
//                                                        user.put("id", uid);
//                                                        user.put("name", name);
//                                                        user.put("email", email);
//                                                        user.put("img", img);
//                                                        db.collection("users")
//                                                                .document(uid).set(user).addOnCompleteListener(
//                                                                dataTask -> {
//                                                                    if(dataTask.isSuccessful()){
//                                                                        goToMain();
//                                                                    }
//                                                                }
//                                                        );
//                                                    }
//                                                }
//                                        );
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            String uid = auth.getCurrentUser().getUid();
//                            HashMap<String, String> user = new HashMap<>();
//                            user.put("id", uid);
//                            user.put("name", name);
//                            user.put("email", email);
//                            user.put("img", "default.png");
//                            db.collection("users")
//                                    .document(uid).set(user).addOnCompleteListener(
//                                    dataTask -> {
//                                        if(dataTask.isSuccessful()){
//                                            goToMain();
//                                        }
//                                    }
//                            );
//                        }
//                    }
//                }
//        );
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.close);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CALLBACK && resultCode == RESULT_OK){
            Uri uri = data.getData();
            path = UtilDomi.getPath(this, uri);

            Bitmap image = BitmapFactory.decodeFile(path);
//            profileImg.setImageBitmap(image);
            profileShapeImg.setImageBitmap(image);
        }
    }
}