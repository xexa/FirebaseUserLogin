package com.example.loginexamplefirebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView fullNameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    private ImageView imagePic;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private StorageReference mStorageRef;

    private Button verifyBtn;
    private TextView messageVerify;

    private Button changeProfileImage;


    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullNameTextView = findViewById(R.id.profileName);
        emailTextView = findViewById(R.id.profileEmail);
        phoneTextView = findViewById(R.id.profilePhone);

        verifyBtn = findViewById(R.id.resendCode);
        messageVerify = findViewById(R.id.verifyMsg);

        changeProfileImage = findViewById(R.id.changeProfile);
        imagePic = findViewById(R.id.profileImage);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = mStorageRef.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imagePic);
            }
        });


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
                if (documentSnapshot.exists()) {
                    phoneTextView.setText(documentSnapshot.getString("phone"));
                    emailTextView.setText(documentSnapshot.getString("email"));
                    fullNameTextView.setText(documentSnapshot.getString("name"));
                }else {
                    Log.d(TAG, "document snapshot doesn't exists");
                }
            }
        });

        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(v.getContext(), EditProfile.class);

                intent.putExtra("fullName", fullNameTextView.getText().toString());
                intent.putExtra("phone", phoneTextView.getText().toString());
                intent.putExtra("email", emailTextView.getText().toString());

                startActivity(intent);

            }
        });
    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}