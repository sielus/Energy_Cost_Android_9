package com.example.energii.koszt.ui.sunEnergyCalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.energii.koszt.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener {
    private static final int REQUEST_CODE = 101 ;
    GoogleMap gMap;
    public static double longitude;
    public static double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);






    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }else {
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);

        }
        gMap.setOnMyLocationButtonClickListener(this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+ " : " + latLng.longitude);
                Toast.makeText(getApplicationContext(), (int) latLng.latitude +" : "+latLng.longitude,Toast.LENGTH_SHORT).show();
                gMap.clear();
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                gMap.addMarker(markerOptions);
                longitude = latLng.longitude;
                latitude = latLng.latitude;
            }


        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    gMap.setMyLocationEnabled(true);
                    gMap.getUiSettings().setMyLocationButtonEnabled(true);
                }else {
                    gMap.setMyLocationEnabled(false);
                }
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this,"zaznacz dokładne dane",Toast.LENGTH_SHORT).show();

        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        this.onBackPressed();
          return true;
    }

    public void onBackPressed() {
        SunEnergyCalculatorFragment sunEnergyCalculatorFragment = new SunEnergyCalculatorFragment();
        sunEnergyCalculatorFragment.setTextViewText(longitude,latitude,SunEnergyCalculatorFragment.root);
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}
