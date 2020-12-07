package com.example.infotransito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private Button signoutBtn;

    private User myUser;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private MapsFragment mapsFragment;
    private ExampleFragment exampleFragment;

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        setContentView(R.layout.activity_main);



        //
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);

//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        if(auth.getCurrentUser() == null){
//            goToLanding();
//            return;
//        }
//
//        db.collection("users").document(auth.getUid()).get().addOnCompleteListener(
//                documentSnapshotTask -> {
//                    myUser = documentSnapshotTask.getResult().toObject(User.class);
//                }
//        );
//
//        signoutBtn = findViewById(R.id.signoutBtn);
//        navigationView = findViewById(R.id.bottomNavigationView);
//        mapsFragment = MapsFragment.newInstance();
//        exampleFragment = ExampleFragment.newInstance();
//
//        showFragment(mapsFragment);
////        showFragment(exampleFragment);
//
//
//        signoutBtn.setOnClickListener(this::signOut);
//        navigationView.setOnNavigationItemSelectedListener(
//                (menuItem) -> {
//                    switch(menuItem.getItemId()){
//                        case R.id.maps:
//                            showFragment(mapsFragment);
//                            break;
//                        case R.id.example:
//                            showFragment(exampleFragment);
//                            break;
//                    }
//                    return true;
//                }
//        );
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
        if(fbUser != null){
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGrant = true;
        if(requestCode==1){
            for(int i=0; i< grantResults.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allGrant = false;
                    break;
                }
            }

            if(!allGrant){
                Toast.makeText(this,"Si no concede todos los permisos, no puede usar " +
                        "la aplicación.", Toast.LENGTH_LONG)
                        .show();
                finish();
                return;
            } else {
                startup();
            }

        }
    }

    public void startup(){
        setContentView(R.layout.activity_main);
        if(auth.getCurrentUser() == null){
            goToLanding();
            return;
        }

        db.collection("users").document(auth.getUid()).get().addOnCompleteListener(
                documentSnapshotTask -> {
                    myUser = documentSnapshotTask.getResult().toObject(User.class);
                }
        );

        signoutBtn = findViewById(R.id.signoutBtn);
        navigationView = findViewById(R.id.bottomNavigationView);
        mapsFragment = MapsFragment.newInstance();
        exampleFragment = ExampleFragment.newInstance();

        showFragment(mapsFragment);
//        showFragment(exampleFragment);


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
                    }
                    return true;
                }
        );
    }
}