package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailWrapper, passwordWrapper;
    private Button loginBtn;
    private TextView signupLink;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrapper);
        loginBtn = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.signupLink);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(this::login);
        signupLink.setOnClickListener(this::goToSignUp);
    }

    private void login(View v){
        String email = emailWrapper.getEditText().getText().toString();
        String password = passwordWrapper.getEditText().getText().toString();

        // Validate data
        if(email.trim().isEmpty()){
            emailWrapper.setError("Escriba un correo.");
            return;
        } else emailWrapper.setError(null);

        if(!TextUtil.isValidEmailAddress(email)){
            emailWrapper.setError("Escriba un correo válido");
            return;
        } else emailWrapper.setError(null);

        if(password.trim().isEmpty()){
            passwordWrapper.setError("Escriba una contraseña");
            return;
        } else passwordWrapper.setError(null);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if(task.isSuccessful()){
                        goToMain();
                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void goToSignUp(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}