package com.example.infotransito;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.UUID;

public class NewMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button backBtn, publishBtn, imgBtn;
    private TextView descripcion, categoria ;
    private ImageView mapIV;

    private String path,nomCategoria;

    private double lat;
    private double lng;

    private String userId;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_marker);

        userId = getIntent().getExtras().getString("userId");
        username = getIntent().getExtras().getString("username");
        nomCategoria = getIntent().getExtras().getString("category");
        lat = getIntent().getExtras().getDouble("lat");
        lng =  getIntent().getExtras().getDouble("lng");


        backBtn = findViewById(R.id.backBtn);
        publishBtn = findViewById(R.id.publishBtn);
        imgBtn = findViewById(R.id.imgBtn);
        descripcion = findViewById(R.id.descripcion);
        categoria = findViewById(R.id.categoria);
        mapIV = findViewById(R.id.mapIV);

        categoria.setText("Categoría: "+nomCategoria);


        backBtn.setOnClickListener(
                (v) -> {
                    exit();
                }
        );

        imgBtn.setOnClickListener(
                v -> {
                    Intent j = new Intent(Intent.ACTION_GET_CONTENT);
                    j.setType("image/*");
                    startActivityForResult(j, 1);
                }
        );

        publishBtn.setOnClickListener(
                (v) -> {
                    String descrip = descripcion.getText().toString();

                    if(path != null) {
                        try {
                            String img = UUID.randomUUID().toString();
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child("marker_images")
                                    .child(img)
                                    .putStream(new FileInputStream(new File(path)))
                                    .addOnCompleteListener(
                                            task -> {
                                                if (task.isSuccessful()) {
                                                    String uid = UUID.randomUUID().toString();
                                                    MapMarker mapMarker = new MapMarker(uid,descrip,nomCategoria,userId,username,lat,lng);
                                                    mapMarker.setTimestamp(new Date().getTime());
                                                    mapMarker.setImg(img);
                                                    FirebaseFirestore.getInstance().collection("markers")
                                                            .document(uid).set(mapMarker).addOnCompleteListener(
                                                            dataTask -> {
                                                                if (dataTask.isSuccessful()) {
                                                                    exit();
                                                                } else {
                                                                    Toast.makeText(this, dataTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                    );
                                                } else {
                                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                    );
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MapMarker mapMarker = new MapMarker(UUID.randomUUID().toString(),descrip,nomCategoria,userId,username,lat,lng);
                        mapMarker.setTimestamp(new Date().getTime());

                        FirebaseFirestore.getInstance()
                                .collection("markers")
                                .document(mapMarker.getId())
                                .set(mapMarker)
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
                }
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void exit(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.close);
    }

    @Override
    public void onBackPressed() {
        exit();
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
        mMap.addMarker(new MarkerOptions().position(sydney).title(nomCategoria));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            path = UtilDomi.getPath(this, uri);

            Bitmap image = BitmapFactory.decodeFile(path);
            mapIV.setImageBitmap(image);
            imgBtn.setText("Cambiar imagen");
        }
    }
}