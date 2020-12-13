package com.example.infotransito;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private static final int CAMERA_CALLBACK = 101 ;
    private static final int GALLERY_CALLBACK = 102;

    private String path;

    private TextInputLayout nameWrapper, emailWrapper, passwordWrapper, repasswordWrapper;
    private Button signupBtn;
    private ImageButton addImgBtn;
//    private ImageView profileImg;
    private ShapeableImageView profileShapeImg;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        profileImg = findViewById(R.id.profileImg);
        profileShapeImg = findViewById(R.id.profileShapeImg);

        nameWrapper = findViewById(R.id.nameWrapper);
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        repasswordWrapper = findViewById(R.id.repasswordWrapper);
        signupBtn = findViewById(R.id.signupBtn);
        addImgBtn = findViewById(R.id.addImgBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(this::signUp);
        addImgBtn.setOnClickListener(this::addImg);
    }

    private void addImg(View view) {
        Intent j = new Intent(Intent.ACTION_GET_CONTENT);
        j.setType("image/*");
        startActivityForResult(j, GALLERY_CALLBACK);
    }

    private void signUp(View v){
        String name = nameWrapper.getEditText().getText().toString();
        String email = emailWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();
        String repassword = repasswordWrapper.getEditText().getText().toString();

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

        if(password.trim().isEmpty()){
            passwordWrapper.setError("Escribe una contraseña");
            return;
        } else passwordWrapper.setError(null);

        if(repassword.trim().isEmpty()){
            repasswordWrapper.setError("Vuelve a escribir la contraseña");
            return;
        } else repasswordWrapper.setError(null);

        if(!password.equals(repassword)){
            repasswordWrapper.setError("La contraseña que escribio no coincide con la inicial");
            return;
        } else repasswordWrapper.setError(null);

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                createTask -> {
                    if(createTask.isSuccessful()){
                        if(path != null) {
                            try {
                                String img = UUID.randomUUID().toString();
                                FirebaseStorage.getInstance()
                                        .getReference()
                                        .child("profile_images")
                                        .child(img)
                                        .putStream(new FileInputStream(new File(path)))
                                .addOnCompleteListener(
                                        task -> {
                                            if(task.isSuccessful()){
                                                String uid = auth.getCurrentUser().getUid();
                                                HashMap<String, String> user = new HashMap<>();
                                                user.put("id", uid);
                                                user.put("name", name);
                                                user.put("email", email);
                                                user.put("img", img);
                                                db.collection("users")
                                                        .document(uid).set(user).addOnCompleteListener(
                                                        dataTask -> {
                                                            if(dataTask.isSuccessful()){
                                                                goToMain();
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String uid = auth.getCurrentUser().getUid();
                            HashMap<String, String> user = new HashMap<>();
                            user.put("id", uid);
                            user.put("name", name);
                            user.put("email", email);
                            user.put("img", "default.png");
                            db.collection("users")
                                    .document(uid).set(user).addOnCompleteListener(
                                    dataTask -> {
                                        if(dataTask.isSuccessful()){
                                            goToMain();
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
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