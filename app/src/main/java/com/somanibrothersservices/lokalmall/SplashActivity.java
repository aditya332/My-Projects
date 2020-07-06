package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;

public class SplashActivity extends AppCompatActivity {
    public static FirebaseUser CURR_USER;
    public static FirebaseFirestore USER_FIREBASE_FIRESTORE;
    public static boolean CITY_CHOSEN;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.logoBackground));

        constraintLayout = findViewById(R.id.splashActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        USER_FIREBASE_FIRESTORE = FirebaseFirestore.getInstance();
        CURR_USER = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setStatusBarColor(getResources().getColor(R.color.logoBackground));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CURR_USER == null) {
                    CITY_CHOSEN = false;
                    startActivity(new Intent(SplashActivity.this, ChooseCityActivity.class));
                    finish();
                } else {
                    USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                CITY = task.getResult().getString("Chosen City");
                                CITY_CHOSEN = true;
                                startActivity(new Intent(SplashActivity.this, ChooseCityActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SplashActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }, 1500);
    }
}
