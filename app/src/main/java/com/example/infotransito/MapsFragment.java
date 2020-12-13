package com.example.infotransito;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import static android.content.Context.LOCATION_SERVICE;
import java.util.ArrayList;


public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private FloatingActionButton police;
    private FloatingActionButton grua;
    private FloatingActionButton camara;
    private FloatingActionButton policeControl;

    private FirebaseFirestore db;
    private ListenerRegistration suscription;
    private ArrayList<MapMarker> marcadores;
    private ArrayList<Marker> pointsMarkers;

    private MainActivity host;

    private GoogleMap mMap;

    // Components
    private Button addBtn;

    // Google Maps
    private LocationManager manager;
    private ArrayList<Marker> markers;

    // Global
    private Position currentPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        host = (MainActivity) getActivity();
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        host = null;
        super.onDetach();
    }

    public MapsFragment(){


    }

    public static MapsFragment newInstance(){
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        db = FirebaseFirestore.getInstance();
        marcadores = new ArrayList<>();
        pointsMarkers = new ArrayList<>();

        police = root.findViewById(R.id.police);
        grua = root.findViewById(R.id.grua);
        camara = root.findViewById(R.id.camara);
        policeControl = root.findViewById(R.id.policeControl);

        police.setOnClickListener(this);
        grua.setOnClickListener(this);
        camara.setOnClickListener(this);
        policeControl.setOnClickListener(this);

        suscribeToMarkers();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            manager = (LocationManager) host.getSystemService(LOCATION_SERVICE);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(checkPermission()){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000,
                    2,
                    this);

            setInitialPos();

            mMap.setOnMarkerClickListener(this);
        }


    }

    @Override
    public void onDestroy() {
        suscription.remove();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateMyLocation(location);
    }

    @SuppressLint("MissingPermission")
    private void setInitialPos() {
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) goToMyLocation(location);
    }

    private void updateMyLocation(Location location) {
        currentPosition = new Position(FirebaseAuth.getInstance().getUid(), location.getLatitude(), location.getLongitude());
    }

    private void goToMyLocation(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentPosition = new Position(FirebaseAuth.getInstance().getUid(), location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(host, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(host, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Ask for the permission
            ActivityCompat.requestPermissions(host, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(host, "Please give location permission", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void suscribeToMarkers(){
        Query references = db.collection("markers");
        suscription = references.addSnapshotListener(
                (data,error) ->{
                    marcadores.clear();
                    pointsMarkers.forEach(
                            (value)->{
                                value.remove();
                            }
                    );

                    for(DocumentSnapshot doc: data.getDocuments()){
                        MapMarker mapMarker = doc.toObject(MapMarker.class);
                        marcadores.add(mapMarker);
                        LatLng latLng = new LatLng(mapMarker.getLat(), mapMarker.getLng());
                        if(mapMarker.getCategory().equals("Policía")){
                            Marker m = mMap.addMarker(new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.policeman)
                            ).anchor(0.5f,0.5f).position(latLng).title(mapMarker.getCategory()));
                            m.setTag(mapMarker);
                            pointsMarkers.add(m);
                        }
                        if(mapMarker.getCategory().equals("Grúa")){
                            Marker m = mMap.addMarker(new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.grua_color)
                            ).anchor(0.5f,0.5f).position(latLng).title(mapMarker.getCategory()));
                            m.setTag(mapMarker);
                            pointsMarkers.add(m);
                        }
                        if(mapMarker.getCategory().equals("Cámara")){
                            Marker m = mMap.addMarker(new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.camara)
                            ).anchor(0.5f,0.5f).position(latLng).title(mapMarker.getCategory()));
                            m.setTag(mapMarker);
                            pointsMarkers.add(m);
                        }
                        if(mapMarker.getCategory().equals("Retén")){
                            Marker m = mMap.addMarker(new MarkerOptions().icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.passport_control)
                            ).anchor(0.5f,0.5f).position(latLng).title(mapMarker.getCategory()));
                            m.setTag(mapMarker);
                            pointsMarkers.add(m);
                        }
                    }

                }
        );
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(host , NewMarkerActivity.class);
        switch (v.getId()){
            case R.id.police:
                i.putExtra("category","Policía");
                i.putExtra("lat",currentPosition.getLat());
                i.putExtra("lng",currentPosition.getLng());
                i.putExtra("userId", host.getMyUser().getId());
                i.putExtra("username", host.getMyUser().getName());
                startActivity(i);
                break;

            case R.id.grua:
                i.putExtra("category","Grúa");
                i.putExtra("lat",currentPosition.getLat());
                i.putExtra("lng",currentPosition.getLng());
                i.putExtra("userId", host.getMyUser().getId());
                i.putExtra("username", host.getMyUser().getName());
                startActivity(i);
                break;

            case R.id.camara:
                i.putExtra("category","Cámara");
                i.putExtra("lat",currentPosition.getLat());
                i.putExtra("lng",currentPosition.getLng());
                i.putExtra("userId", host.getMyUser().getId());
                i.putExtra("username", host.getMyUser().getName());
                startActivity(i);
                break;

            case R.id.policeControl:
                i.putExtra("category","Retén");
                i.putExtra("lat",currentPosition.getLat());
                i.putExtra("lng",currentPosition.getLng());
                i.putExtra("userId", host.getMyUser().getId());
                i.putExtra("username", host.getMyUser().getName());
                startActivity(i);
                break;
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag() != null) {
            MapMarker m = (MapMarker) marker.getTag();
        }

        if(marker.getTag() != null){
            MapMarker m = (MapMarker) marker.getTag();
            Intent i = new Intent(host, ViewMarkerActivity.class);
            i.putExtra("category", m.getCategory());
            i.putExtra("description", m.getContent());
            i.putExtra("lat", m.getLat());
            i.putExtra("lng", m.getLng());
            i.putExtra("img", m.getImg());
            startActivity(i);
        }
        return true;
    }
}