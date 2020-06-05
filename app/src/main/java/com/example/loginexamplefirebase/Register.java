package com.example.loginexamplefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";

    private EditText fullNameEditeText,emailEditText, passwordEditeTex , phoneEditText ;
    private Button registerBtn;
    private TextView loginBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameEditeText = findViewById(R.id.fullName);
        emailEditText = findViewById(R.id.Email);
        passwordEditeTex = findViewById(R.id.password);
        phoneEditText = findViewById(R.id.phone);
        registerBtn = findViewById(R.id.registerBtn);
        loginBtn = findViewById(R.id.createText);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  email = emailEditText.getText().toString().trim();
                final String  password = passwordEditeTex.getText().toString().trim();

                final String fullName = fullNameEditeText.getText().toString().trim();
                final String phone = phoneEditText.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty()){
                    Toast.makeText(Register.this, "PLEASE ENTER EMAIL AND PASSWORD", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password .length() < 6){
                    passwordEditeTex.setError("Password must be greater than 6 characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();

                                    userId = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = db.collection("users").document(userId);

                                    Map<String , Object> user = new HashMap<>();
                                    user.put("name" , fullName);
                                    user.put("email", email);
                                    user.put("phone", phone);

                                    documentReference.set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "user is created " + userId);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }else {
                                    Toast.makeText(Register.this, "Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });


            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }


}