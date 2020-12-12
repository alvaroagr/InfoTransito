package com.example.infotransito;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.UUID;

public class NewMarkerActivity extends AppCompatActivity {

    private Button backBtn, publishBtn, imgBtn;
    private TextView titleET, descripcion, categoria ;

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
        titleET = findViewById(R.id.titleET);
        descripcion = findViewById(R.id.descripcion);
        categoria = findViewById(R.id.categoria);

        categoria.setText("Categoría: "+nomCategoria);


        backBtn.setOnClickListener(
                (v) -> {
                    exit();
                }
        );

        publishBtn.setOnClickListener(
                (v) -> {
                    String title = titleET.getText().toString();
                    String descrip = descripcion.getText().toString();

                    Markerr markerr = new Markerr(UUID.randomUUID().toString(),title,descrip,nomCategoria,userId,username,lat,lng);
                    markerr.setTimestamp(new Date().getTime());

                    FirebaseFirestore.getInstance()
                            .collection("markers")
                            .document(markerr.getId())
                            .set(markerr)
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
        );
    }

    public void exit(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.close);
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}