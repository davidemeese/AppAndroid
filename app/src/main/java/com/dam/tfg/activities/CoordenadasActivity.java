package com.dam.tfg.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.dam.tfg.R;


public class CoordenadasActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;

    TextView tv_Latitud, tv_Longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordenadas);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tv_Longitud = (TextView) findViewById(R.id.tv_Longitud);
        tv_Latitud = (TextView) findViewById(R.id.tv_Latitud);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public void mostrarCoordenadas(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals("Parar")) {
            locationManager.removeUpdates(locationListenerGPS);
            button.setText("Empezar");
        } else {
            if (ActivityCompat.checkSelfPermission(CoordenadasActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);
            button.setText("Parar");
        }
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            tv_Longitud.setText("Longitud: "+longitudeGPS);
            tv_Latitud.setText("Latitud: "+latitudeGPS);
            Toast.makeText(CoordenadasActivity.this, "GPS Provider update", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

/*
    public void mostrarCoordenadas(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals("Parar")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(locationListenerBest);
            button.setText("Empezar");
        } else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListenerBest);
                button.setText("Parar");
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
            }
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_Longitud.setText("Longitud: "+longitudeGPS);
                    tv_Latitud.setText("Latitud: "+latitudeGPS);
                    Toast.makeText(CoordenadasActivity.this, "Best Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };


    public void mostrarCoordenadas(View view) {
        if (!checkLocation())
            return;
        Button button = (Button) view;
        if (button.getText().equals("Parar")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(locationListenerNetwork);
            button.setText("Empezar");
        }
        else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 20 * 1000, 10, locationListenerNetwork);
            Toast.makeText(this, "Network provider started running", Toast.LENGTH_LONG).show();
            button.setText("Parar");
        }
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_Longitud.setText("Longitud: "+longitudeGPS);
                    tv_Latitud.setText("Latitud: "+latitudeGPS);
                    Toast.makeText(CoordenadasActivity.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {

        }
    };

     */

}
