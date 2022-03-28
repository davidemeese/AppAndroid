package com.dam.tfg.activities;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.tfg.R;

import org.json.JSONException;
import org.json.JSONObject;


public class OSMActivity extends AppCompatActivity {
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);
        info = findViewById(R.id.maxvel);
    }

    public void sendVel( View view) {
        String URL = "http://overpass-api.de/api/interpreter?data=[out:json][timeout:25];way[\"maxspeed\"](around:20,37.36839748416125, -5.764459069095468);out;";
        final Integer[] vel = new Integer[1];

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            for(int i = 0; i < response.getJSONArray("elements").length(); i++) {
                                if (i == 0){
                                    info.setText("Velocidad maxima: " + response.getJSONArray("elements").getJSONObject(i).getJSONObject("tags").getString("maxspeed") + " km/h");
                                    vel[0] = Integer.parseInt(response.getJSONArray("elements").getJSONObject(i).getJSONObject("tags").getString("maxspeed"));
                                }
                                else {
                                    if (vel[0] > Integer.parseInt(response.getJSONArray("elements").getJSONObject(i).getJSONObject("tags").getString("maxspeed")))
                                    {
                                        info.setText("Velocidad maxima: " + response.getJSONArray("elements").getJSONObject(i).getJSONObject("tags").getString("maxspeed") + " km/h");
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            info.setText(e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        info.setText(error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
