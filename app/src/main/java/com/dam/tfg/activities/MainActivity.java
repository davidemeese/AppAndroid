package com.dam.tfg.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.dam.tfg.R;
import com.google.android.material.textfield.TextInputEditText;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openLogin(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void sendData(View view) {
        SensorManager sensorManager;
        Sensor accelerometer;
        Sensor gyroscope;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //postData
    public void sendExample(View view){
        TextInputEditText mensaje_edit = (TextInputEditText)findViewById(R.id.mensaje_input);
        TextView textView = (TextView)findViewById(R.id.respuesta);
        String mensaje = mensaje_edit.getText().toString();

        String url = "http://192.168.206.130:8080/api/"+mensaje;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textView.setText("Data: "+response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText("Data: " + error.toString());
                    }
                });
        requestQueue.add(request);
    }
}