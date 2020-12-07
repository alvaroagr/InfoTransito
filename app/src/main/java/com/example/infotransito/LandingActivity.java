package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(this::goToLogin);
        signupBtn.setOnClickListener(this::goToSignUp);
    }

    private void goToLogin(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void goToSignUp(View v){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.enter, R.anim.stay);
    }


}