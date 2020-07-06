package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_PRODUCTS;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false, running_cart_query = false, fromSearch = false;
    public static Activity productDetailsActivity;

    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;

    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private String productOrigignalPrice;
    private TextView prevPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;

    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private TextView productOnlyDescriptionBody;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productOtherDetails;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false, ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addToWishlistButton;

    private TextView tvAddToCartButton;
    private LinearLayout addToCartButton;
    public static MenuItem cartItem;

    private TextView discountedPrice, originalPrice;

    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;

    private Dialog signInDialog, loadingDialog;
    public static String productID;
    private DocumentSnapshot documentSnapshot;
    private TextView badgeCount;
    private Button buyNowBtn;
    private boolean inStock = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.product_images_viewpager_indicator);
        addToWishlistButton = findViewById(R.id.add_to_wishlist_button);

        productTitle = findViewById(R.id.product_images_product_title);
        averageRatingMiniView = findViewById(R.id.product_images_tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.product_images_total_rating_miniview);
        productPrice = findViewById(R.id.product_images_product_price);
        prevPrice = findViewById(R.id.product_images_prev_price);
        codIndicator = findViewById(R.id.product_images_cod_indicator_image_view);
        tvCodIndicator = findViewById(R.id.product_images_cod_indicator_tv);

        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        tvAddToCartButton = findViewById(R.id.tv_add_to_cart_btn);
        addToCartButton = findViewById(R.id.add_to_cart_btn);
        buyNowBtn = findViewById(R.id.buy_now_btn);

        productDetailsViewPager = findViewById(R.id.product_detail_view_pager);
        productDetailsTabLayout = findViewById(R.id.product_detail_tabLayout);

        final List<String> productImages = new ArrayList<>();

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        productID = getIntent().getStringExtra("PRODUCT_ID");
        CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();

                            CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (long x = 1; x < documentSnapshot.getLong("no_of_product_images") + 1; x++) {
                                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                                }
                                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                                productImagesViewPager.setAdapter(productImagesAdapter);

                                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                                getSupportActionBar().setTitle(productTitle.getText().toString());
                                                averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                                totalRatingMiniView.setText("(" + documentSnapshot.get("total_ratings") + ")Ratings");
                                                productPrice.setText(" ₹ " + documentSnapshot.get("product_price"));

                                                //originalPrice.setText( productPrice.getText().toString());
                                                //discountedPrice.setText( productPrice.getText().toString());
                                                productOrigignalPrice = documentSnapshot.get("product_price").toString();

                                                prevPrice.setText(" ₹ " + documentSnapshot.get("prev_price"));
                                                if ((boolean) documentSnapshot.get("COD")) {
                                                    codIndicator.setVisibility(View.VISIBLE);
                                                    tvCodIndicator.setVisibility(View.VISIBLE);
                                                } else {
                                                    codIndicator.setVisibility(View.INVISIBLE);
                                                    tvCodIndicator.setVisibility(View.INVISIBLE);
                                                }
                                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                                    productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                                    productDetailsOnlyContainer.setVisibility(View.GONE);
                                                    productDescription = documentSnapshot.get("product_desc").toString();

                                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                                    for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                        productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                        for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                                            productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                                        }
                                                    }
                                                } else {
                                                    productDetailsTabsContainer.setVisibility(View.GONE);
                                                    productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                                }
                                                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                                                if (CURR_USER != null) {
                                                    if (DBqueries.myRating.size() == 0) {
                                                        DBqueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (DBqueries.cartList.size() == 0) {
                                                        DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                                    }
                                                    if (DBqueries.wishlist.size() == 0) {
                                                        DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
                                                        //DBqueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (DBqueries.cartList.size() != 0 && DBqueries.wishlist.size() != 0) {
                                                        loadingDialog.dismiss();
                                                    }
                                                } else {
                                                    loadingDialog.dismiss();
                                                }
                                                if (DBqueries.cartList.contains(productID)) {
                                                    ALREADY_ADDED_TO_CART = true;
                                                    tvAddToCartButton.setText("GO TO CART");
                                                } else {
                                                    ALREADY_ADDED_TO_CART = false;
                                                    tvAddToCartButton.setText("ADD TO CART");
                                                }
                                                if (DBqueries.wishlist.contains(productID)) {
                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    addToWishlistButton.setSupportImageTintList(getResources().getColorStateList(R.color.buttonRed));
                                                } else {
                                                    ALREADY_ADDED_TO_WISHLIST = false;
                                                }

                                                //if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")) {
                                                inStock = true;
                                                buyNowBtn.setVisibility(View.VISIBLE);
                                                addToCartButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (CURR_USER != null) {
                                                            if (!running_cart_query) {
                                                                running_cart_query = true;
                                                                if (ALREADY_ADDED_TO_CART) {
                                                                    running_cart_query = false;
                                                                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                                                                    MainActivity.showCart = true;
                                                                    startActivity(cartIntent);
                                                                } else {
                                                                    Map<String, Object> addProduct = new HashMap<>();
                                                                    addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                                                    addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));

                                                                    USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_CARTLIST")
                                                                            .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                if (DBqueries.cartItemModelList.size() != 0) {
                                                                                    DBqueries.cartItemModelList.add(0, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                                            , CartItemModel.CART_ITEM
                                                                                            , productID
                                                                                            , documentSnapshot.get("product_image_1").toString()
                                                                                            , documentSnapshot.get("product_title").toString()
                                                                                            , documentSnapshot.get("product_price").toString()
                                                                                            , documentSnapshot.get("prev_price").toString()
                                                                                            , (long) 1
                                                                                            , (long) documentSnapshot.get("max_quantity")
                                                                                            , (long) documentSnapshot.get("stock_quantity")
                                                                                            , inStock));
                                                                                }
                                                                                ALREADY_ADDED_TO_CART = true;
                                                                                tvAddToCartButton.setText("GO TO CART");
                                                                                tvAddToCartButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_shopping_cart_black_24dp, 0);
                                                                                //addToWishlistButton.setSupportImageTintList(getResources().getColorStateList(R.color.buttonRed));
                                                                                DBqueries.cartList.add(productID);
                                                                                Toast.makeText(ProductDetailsActivity.this, "Product Added to Cart !!", Toast.LENGTH_SHORT).show();
                                                                                invalidateOptionsMenu();
                                                                                running_cart_query = false;
                                                                            } else {
                                                                                running_cart_query = false;
                                                                                Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        } else {
                                                            signInDialog.show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_LONG).show();


                        }
                    }
                });

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);
        addToWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURR_USER != null) {
                    //addToWishlistButton.setEnabled(false) ;
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            DBqueries.removeFromWishlist(DBqueries.wishlist.indexOf(productID), ProductDetailsActivity.this);
                            //ALREADY_ADDED_TO_WISHLIST = false;
                            addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        } else {
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.wishlist.size()), productID);
                            addProduct.put("list_size", (long) (DBqueries.wishlist.size() + 1));

                            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (DBqueries.wishlistModelList.size() != 0) {
                                            DBqueries.wishlistModelList.add(
                                                    new WishlistModel(productID,
                                                            documentSnapshot.get("product_image_1").toString()
                                                            , documentSnapshot.get("product_title").toString()
                                                            , documentSnapshot.get("average_rating").toString()
                                                            , documentSnapshot.get("product_price").toString()
                                                            , documentSnapshot.get("prev_price").toString()
                                                            , (boolean) documentSnapshot.get("COD")
                                                            , inStock));
                                        }
                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        addToWishlistButton.setSupportImageTintList(getResources().getColorStateList(R.color.buttonRed));
                                        DBqueries.wishlist.add(productID);
                                        Toast.makeText(ProductDetailsActivity.this, "Product Added to Wishlist !!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });
                        }
                    }
                } else {
                    signInDialog.show();
                }
            }
        });

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURR_USER != null) {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD")
                            , CartItemModel.CART_ITEM
                            , productID
                            , documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , documentSnapshot.get("product_price").toString()
                            , documentSnapshot.get("prev_price").toString()
                            , (long) 1
                            , (long) documentSnapshot.get("max_quantity")
                            , (long) documentSnapshot.get("stock_quantity")
                            , inStock));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog, true);
                    } else {
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                } else {
                    signInDialog.show();
                }
            }
        });

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);
        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.disableCloseButton = false;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.disableCloseButton = false;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CURR_USER != null) {
            if (CURR_USER != null) {
                if (DBqueries.wishlist.size() == 0) {
                    DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
                    //DBqueries.loadRatingList(ProductDetailsActivity.this);
                }
                if (DBqueries.cartList.size() != 0 && DBqueries.wishlist.size() != 0) {
                    loadingDialog.dismiss();
                }
            } else {
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }

        if (DBqueries.myRatedId.contains(productID)) {
            int index = DBqueries.myRatedId.indexOf(productID);
        }

        if (DBqueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
            tvAddToCartButton.setText("GO TO CART");
        } else {
            ALREADY_ADDED_TO_CART = false;
            tvAddToCartButton.setText("ADD TO CART");
        }
        if (DBqueries.wishlist.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishlistButton.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
        } else {
            addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_icon);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.ic_shopping_cart_white_24dp);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (CURR_USER != null) {
            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
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
                if (CURR_USER != null) {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    MainActivity.showCart = true;
                    startActivity(cartIntent);
                } else {
                    signInDialog.show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            if (fromSearch) {
                finish();
            } else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (CURR_USER != null) {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                MainActivity.showCart = true;
                startActivity(cartIntent);
                return true;
            } else {
                signInDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }
}
