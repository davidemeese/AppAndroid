package com.dam.tfg.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dam.tfg.R;
import com.dam.tfg.interfaces.ApiService;
import com.dam.tfg.model.InfraccionData;
import com.dam.tfg.model.UserData;
import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DetectorInfraccionesActivity extends AppCompatActivity {
    private static final String TAG = "GPSyVelActivity";

    LocationManager locationManager;
    double latitude, longitude;
    float velocidadActual, velocidadMaxima;
    TextView lat, lon, vel, vel_max, infra, num, streetId;
    ArcGauge speedometer;
    DecimalFormat dosdeci = new DecimalFormat("#.00");
    DecimalFormat seisdeci = new DecimalFormat("#.000000");
    String s_velocidadActual, fecha;

    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector_infracciones);

        findViewById(R.id.singOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });


        Intent intent = getIntent();
        if (intent.hasExtra("userData")) {
            userData = (UserData) intent.getSerializableExtra("userData");
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        fecha = dateFormat.format(calendar.getTime());

        lat = findViewById(R.id.Lat);
        lon = findViewById(R.id.Lon);
        vel = findViewById(R.id.Vel);
        vel_max = findViewById(R.id.Vel_max);
        infra = findViewById(R.id.Infra);
        streetId = findViewById(R.id.Street);

        speedometer = findViewById(R.id.arcGauge);
        Range range = new Range();
        range.setColor(Color.parseColor("#E12323"));
        range.setFrom(0.0);
        range.setTo(400.0);
        speedometer.addRange(range);

        speedometer.setValueColor(Color.parseColor("#FFFFFFFF"));

        speedometer.setMinValue(0.0);
        speedometer.setMaxValue(500.0);
        speedometer.setValue(10.0);

        int permissionChek = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionChek== PackageManager.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void goBack(){
        Intent intent = new Intent(DetectorInfraccionesActivity.this, LoggedActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " + "usa esta app")
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
        if (!checkLocation()) {
            return;
        }
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
                locationManager.requestLocationUpdates(provider, 5 * 1000, 50, locationListenerBest);
                button.setText("Parar");
            }
        }
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        @SuppressLint("SetTextI18n")
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            runOnUiThread(() -> {
                try {
                    getVelMax(latitude, longitude);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                lon.setText(seisdeci.format(longitude) + "°");
                lat.setText(seisdeci.format(latitude)+ "°");
                if(location.hasSpeed()){
                    velocidadActual = (float) (location.getSpeed()*3.6);
                    s_velocidadActual = dosdeci.format(velocidadActual);
                    vel.setText(s_velocidadActual+" km/h");
                    speedometer.setValue(Math.round(velocidadActual));

                    if(velocidadMaxima != 0.0f && Float.compare(velocidadActual,(velocidadMaxima*2)) > 0){
                        infra.setText("Infraccion cometida: VelocidadMaxima=" + velocidadMaxima*2 +" / Velocidad="+ s_velocidadActual);
                        sendKafka(Double.toString(latitude), Double.toString(longitude), s_velocidadActual);
                        sendFirebase(Double.toString(latitude), Double.toString(longitude), s_velocidadActual, String.valueOf(velocidadMaxima*2));
                        showToast("Infraccion cometida");
                    } else{
                        infra.setText("Infraccion no cometida: VelocidadMaxima=" + velocidadMaxima*2 +" / Velocidad="+ s_velocidadActual);
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

    public void getVelMax(double lat, double lon) throws UnsupportedEncodingException {
        String baseURL = getString(R.string.baseURLVel_extend);
        String data = getString(R.string.dataUrlVel, String.valueOf(lat), String.valueOf(lon));
        String URL = baseURL + URLEncoder.encode(data, "UTF-8");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseURLVel))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.getVelMax(URL);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonArray elementsArray = response.body().getAsJsonArray("elements");
                    for (int i = 0; i < elementsArray.size(); i++) {
                        JsonObject elementObject = elementsArray.get(i).getAsJsonObject();
                        String maxspeed = elementObject.getAsJsonObject("tags").get("maxspeed").getAsString();
                        String street = "";
                        if (elementObject.getAsJsonObject("tags").get("name") != null) {
                            street = elementObject.getAsJsonObject("tags").get("name").getAsString();
                        }

                        if (i == 0) {
                            vel_max.setText(maxspeed + " km/h");
                            streetId.setText("Direccion:" + street);
                            velocidadMaxima = Float.parseFloat(maxspeed);
                        } else {
                            if (velocidadMaxima > Float.parseFloat(maxspeed)) {
                                vel_max.setText(maxspeed + " km/h");
                                streetId.setText("Direccion:" + street);
                                velocidadMaxima = Float.parseFloat(maxspeed);
                            }
                        }
                    }
                } else {
                    Log.e("VelocidadMaxima", "error en la respuesta");
                    vel_max.setText("Error en la respuesta");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("VelocidadMaxima", t.toString());
                vel_max.setText(t.toString());
            }
        });
    }

    public void sendKafka(String latv, String lonv, String velv) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrlKafka))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.sendKafka(latv, lonv, userData.getMatricula(), velv);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Infraccion enviada a Kafka");
                } else {
                    Log.e(TAG, "Error al enviar la infraccion a Kafka");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error al enviar la infraccion a Kafka");
            }
        });
    }

    public void sendFirebase(String latv, String lonv, String velv, String velMaxv) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrlApiFirebase))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        InfraccionData infraccionData = new InfraccionData(fecha,lonv,latv,velv,velMaxv);

        Call<Void> call = apiService.setInfraccion("Bearer "+userData.getToken(), userData.getUserId(),infraccionData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Infraccion guardada correctamente");
                } else {
                    Log.e(TAG, "Error al guardar la infraccion");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error al solicitar guardar la infraccion");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
