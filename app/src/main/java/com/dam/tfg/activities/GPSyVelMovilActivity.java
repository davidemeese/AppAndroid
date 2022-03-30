package com.dam.tfg.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dam.tfg.R;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GPSyVelMovilActivity extends AppCompatActivity {
    LocationManager locationManager;
    double latitude, longitude;
    float velocidadActual, velocidadMaxima;
    TextView lat, lon, vel, calle, vel_max, infra, num;
    DecimalFormat dosdeci = new DecimalFormat("#.00");
    String s_velocidadActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsyvelmovil);

        lat = findViewById(R.id.Lat);
        lon = findViewById(R.id.Lon);
        vel = findViewById(R.id.Vel);
        calle = findViewById(R.id.Calle);
        vel_max = findViewById(R.id.Vel_max);
        infra = findViewById(R.id.Infra);
        num = findViewById(R.id.Num);

        int permissionChek = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionChek== PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                .setPositiveButton("Configuración de ubicación", (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                })
                .setNegativeButton("Cancelar", (paramDialogInterface, paramInt) -> {
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showData(View view) {
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
                locationManager.requestLocationUpdates(provider, 5 * 1000, 100, locationListenerBest);
                button.setText("Parar");
                Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
            }
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            runOnUiThread(() -> {
                lon.setText("Longitud: "+longitude);
                lat.setText("Latitud: "+latitude);
                velMax(latitude, longitude);
                Toast.makeText(GPSyVelMovilActivity.this, "Best Provider update", Toast.LENGTH_SHORT).show();
                setLocation(location);
                if(location.hasSpeed()){
                    velocidadActual = (float) (location.getSpeed()*3.6);
                    s_velocidadActual = dosdeci.format(velocidadActual);
                    vel.setText("Velocidad: "+ s_velocidadActual +"km/h");
                    if(Float.compare(velocidadActual,(velocidadMaxima*2)) > 0){
                        infra.setText("Infraccion: Si " + velocidadMaxima*2 +" "+ s_velocidadActual);
                        num.setText("Enviado a Kafka");
                    }
                    else{
                        infra.setText("Infraccion: No " + velocidadMaxima*2 +" "+ s_velocidadActual);
                        num.setText("No enviado a Kafka");
                    }
                }
            });
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //no-op
        }
        @Override
        public void onProviderEnabled(String s) {
            //no-op
        }
        @Override
        public void onProviderDisabled(String s) {
            //no-op
        }
    };

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    calle.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void velMax( double lat, double lon) {
        String URL = getString(R.string.URL_VelMax,String.valueOf(lat),String.valueOf(lon));

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                response -> {
                    try {
                        for(int i = 0; i < response.getJSONArray("elements").length(); i++) {
                            String maxspeed = response.getJSONArray("elements").getJSONObject(i).getJSONObject("tags").getString("maxspeed");
                            if (i == 0){
                                vel_max.setText("Velocidad maxima: " + maxspeed + " km/h");
                                velocidadMaxima = Float.parseFloat(maxspeed);
                            }
                            else {
                                if (velocidadMaxima > Float.parseFloat(maxspeed))
                                {
                                    vel_max.setText("Velocidad maxima: " + maxspeed + " km/h");
                                    velocidadMaxima = Float.parseFloat(maxspeed);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        vel_max.setText(e.toString());
                    }
                },
                error -> vel_max.setText(error.toString()));

        requestQueue.add(jsonObjectRequest);
    }
}
