package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.tfg.R;
import com.google.android.material.textfield.TextInputEditText;
//TODO crear funcion de login y crear onclick en activitylogin

public class LoginActivity extends AppCompatActivity {

    TextInputEditText userText, passwordText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Inicio de sesión");

        userText = findViewById(R.id.user_input);
        passwordText = findViewById(R.id.pass_input);
        loginButton = findViewById(R.id.button_login);
        loginButton.setOnClickListener(v -> {
            String userName = userText.getText().toString();
            String passwordName = passwordText.getText().toString();

            login(userName, passwordName);
        });
    }

    public void returnMain(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void login(String user, String password) {

    }

}
