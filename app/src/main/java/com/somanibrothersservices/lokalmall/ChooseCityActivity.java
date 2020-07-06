package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.somanibrothersservices.lokalmall.SplashActivity.CITY_CHOSEN;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class ChooseCityActivity extends AppCompatActivity {
    public static String CITY;
    public static FirebaseFirestore CITY_FIREBASE_FIRESTORE;
    private FirebaseOptions options;
    private ConstraintLayout constraintLayout;
    private Button partur , jalna , lokalMallAboutUs;
    private boolean isMallOpen;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        getSupportActionBar().hide();

        constraintLayout = findViewById(R.id.chooseCityActivity);
        partur = findViewById(R.id.button2);
        jalna = findViewById(R.id.button3);
        lokalMallAboutUs = findViewById(R.id.button4);
        lokalMallAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsDialog aboutUsDialog = new AboutUsDialog(ChooseCityActivity.this , false , false);
            }
        });
        loadingDialog = new Dialog(ChooseCityActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CITY_CHOSEN) {
            if (CITY.equals("partur")) {
                partur.performClick();
            } else if (CITY.equals("jalna")) {
                jalna.performClick();
            }
        } else {
            loadingDialog.show();
            USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document("choose city").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Glide.with(ChooseCityActivity.this).load(task.getResult().getString("cities")).into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                constraintLayout.setBackground(resource);
                                loadingDialog.dismiss();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                        getWindow().setStatusBarColor(Color.parseColor(task.getResult().getString("status_bar_color")));
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ChooseCityActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void onClickChooseCityPartur(View view) {
        loadingDialog.show();
        CITY = "partur";
        USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document(CITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    isMallOpen = task.getResult().getBoolean("is_mall_open");
                    if (isMallOpen) {
                        options = new FirebaseOptions.Builder()
                                .setApiKey("AIzaSyDKLpX4jIy3PKjUjouYZy3U8JO8kjt4Y4s")
                                .setProjectId("somani-brothers-services")
                                .setApplicationId("1:438274268883:android:ea16a41200a1404efdbe26")
                                .setDatabaseUrl("https://somani-brothers-services.firebaseio.com/")
                                .setStorageBucket("somani-brothers-services.appspot.com")
                                .build();
                        try
                        {
                            FirebaseApp.initializeApp(ChooseCityActivity.this, options, "Partur");
                        }
                        catch (Exception e)
                        {
                            Log.d("Firebase error", "App already exists");
                        }
                        CITY_FIREBASE_FIRESTORE = FirebaseFirestore.getInstance(FirebaseApp.getInstance("Partur"));
                        startChooseStoreActivity();
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ChooseCityActivity.this, "Lokal Mall at " + CITY.substring(0,1).toUpperCase() + CITY.substring(1).toLowerCase() + " , is opening soon :)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ChooseCityActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onClickChooseCityJalna(View view) {
        loadingDialog.show();
        CITY = "jalna";
        USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document(CITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    isMallOpen = task.getResult().getBoolean("is_mall_open");
                    if (isMallOpen) {
                        options = new FirebaseOptions.Builder()
                                .setApiKey("AIzaSyCx2NJ05dIETeqvFX5U1RWT6FXe_vzP3NU")
                                .setProjectId("lokal-jalna")
                                .setApplicationId("1:438274268883:android:ea16a41200a1404efdbe26")
                                .setDatabaseUrl("https://lokal-jalna.firebaseio.com/")
                                .setStorageBucket("lokal-jalna.appspot.com")
                                .build();
                        try
                        {
                            FirebaseApp.initializeApp(ChooseCityActivity.this, options, "Jalna");
                        }
                        catch (Exception e)
                        {
                            Log.d("Firebase error", "App already exists");
                        }
                        CITY_FIREBASE_FIRESTORE = FirebaseFirestore.getInstance(FirebaseApp.getInstance("Jalna"));
                        startChooseStoreActivity();
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ChooseCityActivity.this, "Lokal Mall at " + CITY.substring(0,1).toUpperCase() + CITY.substring(1).toLowerCase() + " , is opening soon :)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ChooseCityActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startChooseStoreActivity() {
        if (CURR_USER != null && !CITY_CHOSEN) {
            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).update("Chosen City" , CITY).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        CITY_CHOSEN = true;
                        startActivity(new Intent(ChooseCityActivity.this , ChooseStoreActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ChooseCityActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        } else {
            loadingDialog.dismiss();
            startActivity(new Intent(ChooseCityActivity.this , ChooseStoreActivity.class));
        }
    }
}
