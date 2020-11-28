package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button signoutBtn;

    private User myUser;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() == null){
            goToLanding();
            return;
        }

        signoutBtn = findViewById(R.id.signoutBtn);
        signoutBtn.setOnClickListener(this::signOut);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resolveCurrentUser();
    }

    private void resolveCurrentUser() {
        FirebaseUser fbUser = auth.getCurrentUser();
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(
                task -> {
                    DocumentSnapshot snapshot = task.getResult();
                    myUser = snapshot.toObject(User.class);
                    Toast.makeText(this,
                            "Bienvenido " + myUser.getName(),
                            Toast.LENGTH_LONG )
                            .show();
                }
        );

    }

    private void signOut(View v){
        auth.signOut();
        goToLanding();
    }

    private void goToLanding(){
        Intent i = new Intent(this, LandingActivity.class);
        startActivity(i);
        finish();
    }
}