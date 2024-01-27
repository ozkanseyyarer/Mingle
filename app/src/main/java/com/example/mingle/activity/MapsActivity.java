package com.example.mingle.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mingle.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mingle.databinding.ActivityMapsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private ActivityResultLauncher<String> permissionLauncher;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean info;
    private SharedPreferences sharedPreferences;
    private  double selectedLatitude;
    private  double selectedLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        registerLauncher();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = MapsActivity.this.getSharedPreferences("com.example.mingle", MODE_PRIVATE);
        info = false;




        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MapsActivity.this, GezdigimYerlerEkleActvity.class);
                intent.putExtra("latitude",String.valueOf(selectedLatitude));
                intent.putExtra("longitude",String.valueOf(selectedLongitude));
                startActivity(intent);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        binding.saveButton.setEnabled(false);

        Intent getIntent = getIntent();

        if (getIntent.getStringExtra("eskimiyenimi").equals("eski")){

            selectedLatitude = Double.parseDouble(Objects.requireNonNull(getIntent.getStringExtra("latitude")));
            selectedLongitude = Double.parseDouble(Objects.requireNonNull(getIntent.getStringExtra("longitude")));
            binding.saveButton.setVisibility(View.GONE);
            mMap.clear();

            LatLng latLng = new LatLng(selectedLatitude,selectedLongitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

        }
        else if (getIntent.getStringExtra("eskimiyenimi").equals("duzenledenGeldim")){
            selectedLatitude = Double.parseDouble(Objects.requireNonNull(getIntent.getStringExtra("latitude")));
            selectedLongitude = Double.parseDouble(Objects.requireNonNull(getIntent.getStringExtra("longitude")));

            mMap.clear();

            LatLng latLng = new LatLng(selectedLatitude,selectedLongitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

            Snackbar.make(binding.getRoot(), "Eklemek istediğiniz konumun üstüne uzun tıklayınız", 4000).show();
            binding.saveButton.setVisibility(View.VISIBLE);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    info = sharedPreferences.getBoolean("info", false);

                    if (!info) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
                        sharedPreferences.edit().putBoolean("info", true).apply();
                    }


                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Haritalar için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    //burada tekrar izin istenecek
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



                mMap.setMyLocationEnabled(true);
            }

        }
        else{

            Snackbar.make(binding.getRoot(), "Eklemek istediğiniz konumun üstüne uzun tıklayınız", 4000).show();
            binding.saveButton.setVisibility(View.VISIBLE);

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    info = sharedPreferences.getBoolean("info", false);

                    if (!info) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        sharedPreferences.edit().putBoolean("info", true).apply();
                    }


                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Haritalar için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    //burada tekrar izin istenecek
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }

                mMap.setMyLocationEnabled(true);
            }

        }

    }






    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));


        selectedLatitude = latLng.latitude;
        selectedLongitude = latLng.longitude;

        binding.saveButton.setEnabled(true);
    }


    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result) {

                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }
                    }

                } else {
                    Toast.makeText(MapsActivity.this, "İzin gerekli", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("eskimiyenimi").equals("eski")){
            // Eğer saveButton etkin değilse, direkt olarak super metodunu çağırarak aktiviteyi sonlandır
            super.onBackPressed();
        } else {
            Snackbar.make(binding.getRoot(), "Konumu kaydetmeden ayrılmak istediğinize emin misiniz?", Snackbar.LENGTH_LONG)
                    .setAction("Evet", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Kullanıcı onaylarsa, aktiviteyi sonlandırmak için super metodunu çağır
                            MapsActivity.super.onBackPressed();
                        }
                    })
                    .show();
        }
    }
}