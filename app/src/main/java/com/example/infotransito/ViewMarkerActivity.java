package com.example.infotransito;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class ViewMarkerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String userId, markerId, category, description, img;
    private double lat, lng;

    private ImageView categoryIV, mapIV;
    private TextView categoryTV, descriptionTV;
    private Button backBtn, likeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marker);

        category = getIntent().getExtras().getString("category");
        description = getIntent().getExtras().getString("description");
        img = getIntent().getExtras().getString("img");
        lat = getIntent().getExtras().getDouble("lat");
        lng = getIntent().getExtras().getDouble("lng");


        categoryIV = findViewById(R.id.categoryIV);
        mapIV = findViewById(R.id.mapIV);
        categoryTV = findViewById(R.id.categoryTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        backBtn = findViewById(R.id.backBtn);
        likeBtn = findViewById(R.id.likeBtn);

        categoryTV.setText(category);
        descriptionTV.setText(description);

        switch(category){
            case "Policía":
                categoryIV.setImageResource(R.drawable.policeman);
                break;
            case "Grúa":
                categoryIV.setImageResource(R.drawable.grua_color);
                break;
            case "Cámara":
                categoryIV.setImageResource(R.drawable.camara);
                break;
            case "Retén":
                categoryIV.setImageResource(R.drawable.passport_control);
                break;
        }

        backBtn.setOnClickListener(
                v -> finish()
        );

//        Query q = FirebaseFirestore.getInstance()
//                .collection("likes")
//                .document(userId)
//                .collection("markers")
//                .whereEqualTo("id", markerId);
//
//        q.get().addOnCompleteListener(
//                task -> {
//                    if(task.isSuccessful()){
//                        if(task.getResult().size() == 0){
//                            likeBtn.setOnClickListener(
//                                    v -> {
//                                        HashMap<String, String> like = new HashMap<>();
//                                        like.put("")
//
//
//
//
//
//
//                                        FirebaseFirestore.getInstance()
//                                                .collection("markers")
//                                                .document(markerId)
//                                                .update("likes", FieldValue.increment(1))
//                                                .addOnCompleteListener(
//                                                        inTask -> {
//                                                            if(inTask.isSuccessful()){
//                                                                FirebaseFirestore.getInstance()
//                                                                        .collection("likes")
//                                                                        .document(userId)
//                                                                        .collection("markers")
//                                                            }
//                                                        }
//                                                );
//
//
//
//                                        Toast.makeText(this, "This button works.", Toast.LENGTH_LONG).show();
//
//
//
//
//
//
//
//                                    }
//                            );
//
//                        } else {
//
//                        }
//
//                    }
//                }
//        );

        likeBtn.setOnClickListener(
                v -> {
                    Toast.makeText(this, "¡La información le fue util!", Toast.LENGTH_LONG).show();
                }
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(img != null){
            FirebaseStorage.getInstance().getReference().child("marker_images").child(img).getDownloadUrl().addOnCompleteListener(
                    task -> {
                        String url = task.getResult().toString();
                        Glide.with(mapIV).load(url).into(mapIV);
                    }
            );
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(false);

        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title(category));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
    }

}