package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.tfg.R;
import com.dam.tfg.interfaces.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoggedActivity extends AppCompatActivity {

    private static final String TAG = "LoggedActivity";

    String userId, token;

    TextInputEditText matriculaTextInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        setTitle("Inicio de sesión");

        String url = getString(R.string.URL_DATABASE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        currentUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        token = task.getResult().getToken();
                        getMatricula(token);
                        setToken(token);
                        Log.d("Token", token);
                    }
                });

        userId = currentUser.getUid();

        matriculaTextInput = findViewById(R.id.matricula_input);

        findViewById(R.id.singOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        findViewById(R.id.enviarMatricula).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMatricula();
            }
        });
        findViewById(R.id.mostrarInfracciones).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAInfracciones(view);
            }
        });
    }

    private void getMatricula(String token) {
        String baseUrl = getString(R.string.baseUrlApiFirebase);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getMatriculaByUserId("Bearer "+token,userId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    matriculaTextInput.setText(response.body());
                } else {
                    matriculaTextInput.setText("Introduce matricula");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                matriculaTextInput.setText("Fallo");
            }
        });
    }

    private void saveMatricula() {
        if (matriculaTextInput.getText() != null) {
            String baseUrl = getString(R.string.baseUrlApiFirebase);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<Void> call = apiService.setMatriculaByUserId("Bearer "+token, userId, matriculaTextInput.getText().toString());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Matrícula guardada correctamente");
                    } else {
                        Log.e(TAG, "Error al guardar la matrícula");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error al solicitar guardar la matrícula");
                }
            });
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(LoggedActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void irAEnvio(View view){
        Intent intent = new Intent(this, GPSyVelActivity.class);
        intent.putExtra("matricula", this.matriculaTextInput.getText().toString());
        intent.putExtra("token", token);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void irAInfracciones(View view) {
        Intent intent = new Intent(this, InfraccionesActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void setToken(String token) {
        this.token = token;
    }
}
