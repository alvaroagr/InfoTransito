package com.example.infotransito;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

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

        // View References
        addBtn = root.findViewById(R.id.addBtn);

        markers = new ArrayList<>();

        //
        addBtn.setOnClickListener(
                v -> {
                    Toast.makeText(root.getContext(), "Map Button works", Toast.LENGTH_LONG).show();
                }
        );
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

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    2,
                    this);

            setInitialPos();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateMyLocation(location);
    }

    @SuppressLint("MissingPermission")
    private void setInitialPos() {
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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


}