package com.dam.tfg.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dam.tfg.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;


public class MapActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private TextView tvLatitud, tvLongitud, tvAltura, tvPrecision;
    private LocationManager locManager;

    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        tvLatitud = (TextView) findViewById(R.id.tvLatitud);
        tvLongitud = (TextView) findViewById(R.id.tvLongitud);
        tvAltura = (TextView) findViewById(R.id.tvAltura);
        tvPrecision = (TextView) findViewById(R.id.tvPrecision);

        int permissionChek = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionChek==PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void show(View view){
        LocationManager locationManager = (LocationManager) MapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                tvLatitud.setText("Latitud: " + location.getLatitude());
                tvLongitud.setText("Longitud: " + location.getLongitude());
                tvAltura.setText("Altura: " + location.getAltitude());
                tvPrecision.setText("Precision: " + location.getAccuracy());
            }
        };

        int permissionChek = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
}
