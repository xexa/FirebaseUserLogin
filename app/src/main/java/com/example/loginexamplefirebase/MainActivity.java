package com.example.loginexamplefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView fullNameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    private ImageView imagePic;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Button verifyBtn;
    private TextView messageVerify;


    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullNameTextView = findViewById(R.id.full_name);
        emailTextView = findViewById(R.id.email_text_view);
        phoneTextView = findViewById(R.id.phone_text_view);

        verifyBtn = findViewById(R.id.verify_button);
        messageVerify = findViewById(R.id.textView7);

        imagePic = findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();


        if (!firebaseUser.isEmailVerified()){
            messageVerify.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseUser.sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Verification link sent to your email", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                                }
                            });
                }
            });

        }

        DocumentReference documentReference = db.collection("users").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                phoneTextView.setText(documentSnapshot.getString("phone"));
                emailTextView.setText(documentSnapshot.getString("email"));
                fullNameTextView.setText(documentSnapshot.getString("name"));
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}