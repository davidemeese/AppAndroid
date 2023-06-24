package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.tfg.R;
import com.dam.tfg.adapter.InfraccionAdapter;
import com.dam.tfg.interfaces.ApiService;
import com.dam.tfg.interfaces.InfraccionCallback;
import com.dam.tfg.model.InfraccionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfraccionesActivity extends AppCompatActivity implements InfraccionCallback {

    private final String TAG = "InfraccionesActivity";

    private RecyclerView recyclerView;
    private InfraccionAdapter infraccionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infracciones);

        String token = getIntent().getStringExtra("token");
        String userId = getIntent().getStringExtra("userId");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getInfracciones(token, userId, this);

        findViewById(R.id.singOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    private void goBack(){
        Intent intent = new Intent(InfraccionesActivity.this, LoggedActivity.class);
        startActivity(intent);
        finish();
    }

    public void getInfracciones(String token, String userId, InfraccionCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrlApiFirebase))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<InfraccionData>> call = apiService.getInfracciones("Bearer "+token, userId);
        call.enqueue(new Callback<List<InfraccionData>>() {
            @Override
            public void onResponse(Call<List<InfraccionData>> call, Response<List<InfraccionData>> response) {
                if (response.isSuccessful()) {
                    List<InfraccionData> infracciones = response.body();
                    if (infracciones != null) {
                        callback.onInfraccionesObtenidas(infracciones);
                    }
                } else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<InfraccionData>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void onInfraccionesObtenidas(List<InfraccionData> infracciones) {
        infraccionAdapter = new InfraccionAdapter(infracciones);
        recyclerView.setAdapter(infraccionAdapter);
    }

    @Override
    public void onError(String mensajeError) {
        // Manejar el error, por ejemplo, mostrar un mensaje de error al usuario
        Log.d(TAG, mensajeError);;
    }

}
