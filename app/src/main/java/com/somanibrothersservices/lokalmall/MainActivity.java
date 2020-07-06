package com.somanibrothersservices.lokalmall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int ACCOUNT_FRAGMENT = 4;
    public static Boolean showCart = false, resetMainActivity = false , ordered;
    public static Activity mainActivity;
    private FrameLayout frameLayout;
    private int currentFragment = -1;
    private NavigationView navigationView;
    //private ImageView actionBarLogo;
    private CircleImageView profileView;
    private TextView fullName, mobileNo;

    private Window window;
    private Toolbar toolbar;
    private Dialog signInDialog;

    public static DrawerLayout drawer;
    private TextView badgeCount, notifyCount;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        //actionBarLogo = findViewById(R.id.actionbar_logo) ;
        setSupportActionBar(toolbar);
        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        FirebaseInAppMessaging.getInstance().triggerEvent("main_activity_ready");
        FirebaseMessaging.getInstance().subscribeToTopic(CITY+STORE)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        frameLayout = findViewById(R.id.main_framelayout);

        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_imageview_profile_image);
        fullName = navigationView.getHeaderView(0).findViewById(R.id.main_textview_fullname);
        mobileNo = navigationView.getHeaderView(0).findViewById(R.id.main_textView_mobile_no);

        if (showCart) {
            mainActivity = this;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);

        } else {
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.myCartFragment, R.id.myOrdersFragment, R.id.myWishlistFragment, R.id.myAccountFragment)
                    .setDrawerLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);
        final Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                if (CURR_USER != null) {
                    drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                            super.onDrawerSlide(drawerView, slideOffset);
                            int id = item.getItemId();

                            if (id == R.id.nav_home) {
                                item.setChecked(true);
                                invalidateOptionsMenu();
                                setFragment(new HomeFragment(), HOME_FRAGMENT);
                            } else if (id == R.id.myOrdersFragment) {
                                if (!ordered) {
                                    Toast.makeText(MainActivity.this, "You have to Order something to access this segment :) ", Toast.LENGTH_LONG).show();
                                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                                } else {
                                    gotoFragment("My Orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
                                }
                            } else if (id == R.id.myCartFragment) {
                                //Navigation.createNavigateOnClickListener(R.id.nav_my_cart) ;
                                //item.setChecked(true) ;
                                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                            } else if (id == R.id.myWishlistFragment) {
                                gotoFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                            } else if (id == R.id.myAccountFragment) {
                                if (!ordered) {
                                    Toast.makeText(MainActivity.this, "You have to Order something to access this segment :) ", Toast.LENGTH_LONG).show();
                                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                                } else {
                                    gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                                }
                            }
                            drawerLayout.removeDrawerListener(this);
                        }
                    });
                    return true;
                } else {
                    signInDialog.show();
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_shopping_cart_white_24dp);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
            if (CURR_USER != null) {
                if (DBqueries.cartList.size() == 0) {
                    //badgeCount.setVisibility(View.INVISIBLE) ;
                    DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false, badgeCount, new TextView(MainActivity.this));
                    //DBqueries.loadRatingList(ProductDetailsActivity.this);
                } else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    } else {
                        badgeCount.setText("++");
                    }
                }
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CURR_USER == null) {
                        signInDialog.show();
                    } else {
                        gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                    }
                }
            });

            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView badgeIconNoti = notifyItem.getActionView().findViewById(R.id.badge_icon);
            badgeIconNoti.setImageResource(R.drawable.ic_notifications_white_24dp);
            notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);
            if (CURR_USER != null) {
                DBqueries.checkNotifications(false, notifyCount);
            }
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }
            });
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CURR_USER == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
//            if (DBqueries.mobileNo == null) {
                USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.fullName = task.getResult().getString("fullName");
                            DBqueries.mobileNo = task.getResult().getString("mobileNo");
                            DBqueries.profile = task.getResult().getString("profile");

                            fullName.setText(DBqueries.fullName);
                            mobileNo.setText(DBqueries.mobileNo);
                            if (DBqueries.profile.equals("")) {
                                //addProfileIcon.setVisibility(View.VISIBLE);
                            } else {
                                //addProfileIcon.setVisibility(View.INVISIBLE);
                                Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(profileView);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            /*} else {
                fullName.setText(DBqueries.fullName);
                mobileNo.setText(DBqueries.mobileNo);
                if (DBqueries.profile.equals("")) {
                    profileView.setImageResource(R.drawable.ic_person_outline_black_24dp);
                    //addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    //addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(profileView);
                }
            }*/
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        if (resetMainActivity) {
            //actionBarLogo.setVisibility(View.VISIBLE);
            resetMainActivity = false;
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBqueries.checkNotifications(true, null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
                DBqueries.clearData();
                finish();
            } else {
                if (showCart) {
                    mainActivity = null;
                    showCart = false;
                    finish();
                } else {
                    //actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_search_icon) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == R.id.main_notification_icon) {
            startActivity(new Intent(this, NotificationActivity.class));
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (CURR_USER == null) {
                signInDialog.show();
            } else {
                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
            }
            return true;
        } else if (id == android.R.id.home) {
            if (showCart) {
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        //actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(1).setChecked(true);
            params.setScrollFlags(0);
        } else {
            params.setScrollFlags(scrollFlags);
        }
    }
}
