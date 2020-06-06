package com.example.loginexamplefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginBtn;
    private TextView mCreateBtn;
    private ProgressBar progressBar;

    private TextView forgetPasswordTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        progressBar = findViewById(R.id.progressBar);

        forgetPasswordTextView = findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  email = emailEditText.getText().toString().trim();
                String  password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty()){
                    Toast.makeText(Login.this, "PLEASE ENTER EMAIL AND PASSWORD", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password .length() < 6){
                    passwordEditText.setError("Password must be greater than 6 characters");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Login.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                }else {
                                    Toast.makeText(Login.this, "Error: "+ task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetEmail = new EditText(v.getContext());

                AlertDialog.Builder passwordDialogAlert = new AlertDialog.Builder(v.getContext());
                passwordDialogAlert.setTitle("Reset Password ?");
                passwordDialogAlert.setMessage("Enter your email");
                passwordDialogAlert.setView(resetEmail);

                passwordDialogAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //abstract email and send email link

                        String email = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Login.this, "Link send to your email", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                passwordDialogAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
                passwordDialogAlert.create().show();
            }
        });
    }
}