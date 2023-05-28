package com.dam.tfg.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.tfg.R;
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

public class LoggedActivity extends AppCompatActivity {

    private static final String TAG = "LoggedActivity";

    String userId, token;
    FirebaseUser currentUser;
    TextInputEditText matriculaTextInput;

    DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        setTitle("Inicio de sesión");

        String url = getString(R.string.URL_DATABASE);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(url);
        usersRef = firebaseDatabase.getReference("usuarios");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(Task<GetTokenResult> task) {
                        token = task.getResult().getToken();
                        Log.i("token", token);
                    }
                });

        userId = currentUser.getUid();

        matriculaTextInput = findViewById(R.id.matricula_input);
        if (matriculaTextInput.getText() == null) {
            getMatricula();
        } else if (matriculaTextInput.getText().toString().equals("")) {
            getMatricula();
        }

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
    }

    private void getMatricula() {
        usersRef.child(userId).child("matricula").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                matriculaTextInput.setText(value);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void saveMatricula() {
        if (matriculaTextInput.getText() != null) {
            String matricula = matriculaTextInput.getText().toString();
            usersRef.child(userId).child("matricula").setValue(matricula)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Matrícula guardada correctamente");
                            } else {
                                Log.e(TAG, "Error al guardar la matrícula: " + task.getException().getMessage());
                            }
                        }
                    });
        }
        Log.d(TAG, "usersRef:"+usersRef);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(LoggedActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void irAEnvio(View view){
        Intent intent = new Intent(this, GPSyVelActivity.class);
        startActivity(intent);
    }
}
