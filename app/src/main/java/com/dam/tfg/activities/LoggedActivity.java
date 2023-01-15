package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.tfg.R;

public class LoggedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        setTitle("Inicio de sesión");
    }

    public void irAEnvio(View view){
        Intent intent = new Intent(this, GPSyVelActivity.class);
        startActivity(intent);
    }
}
