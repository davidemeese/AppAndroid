package com.dam.tfg.activities;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.tfg.R;

import org.json.JSONException;


public class OSMActivity extends AppCompatActivity {
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);
        info = findViewById(R.id.maxvel);
    }

    public void sendVel( View view) {
        String URL = "https://z.overpass-api.de/api/interpreter?data=%5Bout%3Ajson%5D%5Btimeout%3A25%5D%3B%28way%5B%22maxspeed%22%5D%28around%3A10%2C37%2E3255756%2C%2D5%2E7838223%29%3B%29%3Bout%3B%3E%3B%0A";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    Log.d("Volley", response.toString());
                    try {
                        info.setText("Velocidad maxima: "+response.getJSONArray("elements").getJSONObject(0).getJSONObject("tags").getString("maxspeed")+" km/h");
                    } catch (JSONException e) {
                        info.setText(e.toString());
                    }
                },
                error -> {
                    info.setText(error.toString());
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
