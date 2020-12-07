package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button signoutBtn;

    private User myUser;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private MapsFragment mapsFragment;
    private ExampleFragment exampleFragment;
    private ForumFragment forumFragment;

    private BottomNavigationView navigationView;

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
        navigationView = findViewById(R.id.bottomNavigationView);
        mapsFragment = MapsFragment.newInstance();
        exampleFragment = ExampleFragment.newInstance();
        forumFragment = ForumFragment.newInstance();

        showFragment(mapsFragment);


        signoutBtn.setOnClickListener(this::signOut);
        navigationView.setOnNavigationItemSelectedListener(
                (menuItem) -> {
                    switch(menuItem.getItemId()){
                        case R.id.maps:
                            showFragment(mapsFragment);
                            break;
                        case R.id.example:
                            showFragment(exampleFragment);
                            break;
                        case R.id.forum:
                            showFragment(forumFragment);
                            break;
                    }
                    return true;
                }
        );
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
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
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}