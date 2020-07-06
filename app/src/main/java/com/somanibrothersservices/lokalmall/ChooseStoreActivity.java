package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.SplashActivity.CITY_CHOSEN;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class ChooseStoreActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    public static String STORE , STORE_ORDERS , STORE_CATEGORIES , STORE_PRODUCTS , STORE_USER_DATA , STORE_USER_ORDERS , MIN_AMOUNT , STORE_NAME , STORE_ABOUT_US_BODY , STORE_WELCOME_STATEMENT , STORE_UPI_ID;
    public static boolean ACCEPTS_UPI;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_store);

        getSupportActionBar().hide();

        constraintLayout = findViewById(R.id.chooseStoreActivity);
        loadingDialog = new Dialog(ChooseStoreActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadingDialog.show();
        FirebaseMessaging.getInstance().subscribeToTopic(CITY)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        USER_FIREBASE_FIRESTORE.collection("LOKAL MALL").document(CITY).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Glide.with(ChooseStoreActivity.this).load(task.getResult().getString("mall_image")).into(new CustomTarget<Drawable>() {
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
                    Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final SparkButton somaniStoreButton = findViewById(R.id.somani_store_button);
        final SparkButton rasikaStoreButton = findViewById(R.id.rasika_store_button);
        somaniStoreButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                loadingDialog.show();
                if (CITY.equals("partur")) {
                    STORE = "SOMANI";
                    STORE_CATEGORIES = "CATEGORIES";
                    STORE_ORDERS = "ORDERS";
                    STORE_PRODUCTS = "PRODUCTS";
                    STORE_USER_DATA = "PARTUR/SOMANI/USER_DATA";
                    STORE_USER_ORDERS = "PARTUR/SOMANI/USER_ORDERS";
                    CITY_FIREBASE_FIRESTORE.collection(STORE).document(STORE).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getBoolean("is_store_open")) {
                                    ACCEPTS_UPI = task.getResult().getBoolean("accepts_upi");
                                    MIN_AMOUNT = task.getResult().getString("min amount");
                                    STORE_NAME = task.getResult().getString("store_name");
                                    STORE_ABOUT_US_BODY = task.getResult().getString("body");
                                    STORE_WELCOME_STATEMENT = task.getResult().getString("welcome_statement");
                                    STORE_UPI_ID = task.getResult().getString("upi_id");
                                    if (CURR_USER == null) {
                                        startActivity(new Intent(ChooseStoreActivity.this, MainActivity.class));
                                        loadingDialog.dismiss();
                                    } else {
                                        updateUser();
                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ChooseStoreActivity.this, "Opening Soon :)", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (CITY.equals("jalna")) {
                    STORE = "RASIKA";
                    STORE_CATEGORIES = "RASIKA/RASIKA/CATEGORIES";
                    STORE_ORDERS = "RASIKA/RASIKA/ORDERS";
                    STORE_PRODUCTS = "RASIKA/RASIKA/PRODUCTS";
                    STORE_USER_DATA = "JALNA/RASIKA/USER_DATA";
                    STORE_USER_ORDERS = "JALNA/RASIKA/USER_ORDERS";
                    CITY_FIREBASE_FIRESTORE.collection(STORE).document(STORE).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getBoolean("is_store_open")) {
                                    ACCEPTS_UPI = task.getResult().getBoolean("accepts_upi");
                                    MIN_AMOUNT = task.getResult().getString("min amount");
                                    STORE_NAME = task.getResult().getString("store_name");
                                    STORE_ABOUT_US_BODY = task.getResult().getString("body");
                                    STORE_WELCOME_STATEMENT = task.getResult().getString("welcome_statement");
                                    STORE_UPI_ID = task.getResult().getString("upi_id");
                                    if (CURR_USER == null) {
                                        startActivity(new Intent(ChooseStoreActivity.this, MainActivity.class));
                                        loadingDialog.dismiss();
                                    } else {
                                        updateUser();
                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ChooseStoreActivity.this, "Opening Soon :)", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                somaniStoreButton.setChecked(false);
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
        rasikaStoreButton.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                loadingDialog.show();
                if (CITY.equals("partur")) {
                    STORE = "RASIKA";
                    STORE_CATEGORIES = "RASIKA/RASIKA/CATEGORIES";
                    STORE_ORDERS = "RASIKA/RASIKA/ORDERS";
                    STORE_PRODUCTS = "RASIKA/RASIKA/PRODUCTS";
                    STORE_USER_DATA = "PARTUR/RASIKA/USER_DATA";
                    STORE_USER_ORDERS = "PARTUR/RASIKA/USER_ORDERS";
                    CITY_FIREBASE_FIRESTORE.collection(STORE).document(STORE).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getBoolean("is_store_open")) {
                                    ACCEPTS_UPI = task.getResult().getBoolean("accepts_upi");
                                    MIN_AMOUNT = task.getResult().getString("min amount");
                                    STORE_NAME = task.getResult().getString("store_name");
                                    STORE_ABOUT_US_BODY = task.getResult().getString("body");
                                    STORE_WELCOME_STATEMENT = task.getResult().getString("welcome_statement");
                                    STORE_UPI_ID = task.getResult().getString("upi_id");
                                    if (CURR_USER == null) {
                                        startActivity(new Intent(ChooseStoreActivity.this, MainActivity.class));
                                        loadingDialog.dismiss();
                                    } else {
                                        updateUser();
                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ChooseStoreActivity.this, "Opening Soon :)", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if (CITY.equals("jalna")) {
                    Toast.makeText(ChooseStoreActivity.this, "Opening Soon :)", Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
                rasikaStoreButton.setChecked(false);
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });
    }

    public void onClickAboutStore(View view) {
        AboutUsDialog aboutUsDialog = new AboutUsDialog(ChooseStoreActivity.this , false , true);
    }

    public void updateUser() {
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> ordered = new ArrayList<>();
                    ordered = (List<String>)task.getResult().get("ordered");
                    if (ordered.contains(CITY + STORE)) {
                        MainActivity.ordered = true;
                    } else {
                        MainActivity.ordered = false;
                    }
                    List<String> visited = new ArrayList<>();
                    visited = (List<String>) task.getResult().get("visited");
                    if (visited.contains(CITY + STORE)) {
                        startActivity(new Intent(ChooseStoreActivity.this , MainActivity.class));
                    } else {
                        visited.add(CITY + STORE);
                        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).update("visited" , visited)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Map<String, Object> wishlistMap = new HashMap<>();
                                            wishlistMap.put("list_size", (long) 0);

                                            Map<String, Object> myRatingsMap = new HashMap<>();
                                            myRatingsMap.put("list_size", (long) 0);

                                            Map<String, Object> myCartMap = new HashMap<>();
                                            myCartMap.put("list_size", (long) 0);

                                            Map<String, Object> myAddressesMap = new HashMap<>();
                                            myAddressesMap.put("list_size", (long) 0);

                                            Map<String, Object> notificationsMap = new HashMap<>();
                                            notificationsMap.put("list_size", (long) 0);

                                            final List<String> documentNames = new ArrayList<>();
                                            documentNames.add("MY_WISHLIST");
                                            documentNames.add("MY_RATINGS");
                                            documentNames.add("MY_CARTLIST");
                                            documentNames.add("MY_NOTIFICATIONS");

                                            final List<Map<String, Object>> documentFields = new ArrayList<>();
                                            documentFields.add(wishlistMap);
                                            documentFields.add(myRatingsMap);
                                            documentFields.add(myCartMap);
                                            documentFields.add(notificationsMap);

                                            for (int x = 0; x < documentNames.size(); x++) {
                                                final int finalX = x;
                                                USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid())
                                                        .collection(STORE_USER_DATA).document(documentNames.get(x))
                                                        .set(documentFields.get(x))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (finalX == (documentNames.size() - 1)) {
                                                                        startActivity(new Intent(ChooseStoreActivity.this , MainActivity.class));
                                                                        loadingDialog.dismiss();
                                                                    }
                                                                } else {
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(ChooseStoreActivity.this, error, Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        } else {
                                            loadingDialog.dismiss();
                                            Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ChooseStoreActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickChangeCity(View view) {
        CITY_CHOSEN = false;
        startActivity(new Intent(ChooseStoreActivity.this , ChooseCityActivity.class));
        loadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (CITY_CHOSEN) {
            moveTaskToBack(true);
        }
    }
}
