package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout nameWrapper, emailWrapper, passwordWrapper, repasswordWrapper;
    private Button signupBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameWrapper = findViewById(R.id.nameWrapper);
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        repasswordWrapper = findViewById(R.id.repasswordWrapper);
        signupBtn = findViewById(R.id.signupBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(this::signUp);
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
                task -> {
                    if(task.isSuccessful()){
                        String uid = auth.getCurrentUser().getUid();
                        HashMap<String, String> user = new HashMap<>();
                        user.put("id", uid);
                        user.put("name", name);
                        user.put("email", email);

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
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.close);
    }
}