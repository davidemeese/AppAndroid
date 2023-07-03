package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.tfg.R;
import com.dam.tfg.interfaces.ApiService;
import com.dam.tfg.model.UserData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoggedActivity extends AppCompatActivity {

    private static final String TAG = "LoggedActivity";

    UserData userData;

    TextInputEditText matriculaTextInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        setTitle("Inicio de sesión");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = currentUser.getUid();

        matriculaTextInput = findViewById(R.id.matricula_input);

        currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        showMatricula(token, userId);
                        setUserData(token, userId, matriculaTextInput);
                    }
                });

        findViewById(R.id.singOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        findViewById(R.id.enviarMatricula).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (matriculaTextInput.getText() != null && !matriculaTextInput.getText().toString().toLowerCase().contains("matricula")) {
                    saveMatricula();
                } else {
                    showToast("Introduce una matrícula para guardar");
                }
            }
        });

        findViewById(R.id.mostrarInfracciones).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userData.getMatricula().toLowerCase().contains("matricula")) {
                    irAInfracciones(view);
                } else {
                    showToast("Usuario sin matricula: guarda una matricula");
                }

            }
        });

        findViewById(R.id.iraDeteccion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userData.getMatricula().toLowerCase().contains("matricula") && !userData.getMatricula().equals("")) {
                    irADeteccion(view);
                } else {
                    showToast("Usuario sin matricula: guarda una matricula");
                }
            }
        });
    }

    private void showMatricula(String token, String userId) {
        String baseUrl = getString(R.string.baseUrlApiFirebase);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getMatriculaByUserId("Bearer "+token, userId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    matriculaTextInput.setText(response.body());
                    userData.setMatricula(response.body());
                } else {
                    matriculaTextInput.setText("Introduce matricula");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                matriculaTextInput.setText("Introduce matricula");
            }
        });
    }

    private void saveMatricula() {
        String baseUrl = getString(R.string.baseUrlApiFirebase);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Void> call = apiService.setMatriculaByUserId("Bearer " + userData.getToken(), userData.getUserId(), userData.getMatricula());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    userData.setMatricula(matriculaTextInput.getText().toString());
                    Log.d(TAG, "Matrícula guardada correctamente");
                    showToast("Matrícula guardada correctamente");
                } else {
                    Log.e(TAG, "Error al guardar la matrícula");
                    showToast("Error al guardar la matrícula");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error al guardar la matrícula");
                showToast("Error al guardar la matrícula");
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(LoggedActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void irADeteccion(View view){
        Intent intent = new Intent(this, DetectorInfraccionesActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
    }

    public void irAInfracciones(View view) {
        Intent intent = new Intent(this, HistorialInfraccionesActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
    }

    private void setUserData(String token, String userId, TextInputEditText matriculaTextInput) {
        this.userData = new UserData(userId, token, matriculaTextInput.getText().toString());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
