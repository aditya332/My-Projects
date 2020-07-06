package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_CATEGORIES;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_ORDERS;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_PRODUCTS;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_ORDERS;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class DBqueries {
    public static String mobileNo , fullName , profile ;
    public static List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadCategoriesNames = new ArrayList<>();
    public static List<String> wishlist = new ArrayList<>() ;
    public static List<WishlistModel> wishlistModelList = new ArrayList<>() ;
    public static List<String> cartList = new ArrayList<>() ;
    public static List<CartItemModel> cartItemModelList = new ArrayList<>() ;
    public static List<String> myRatedId = new ArrayList<>() ;
    public static List<Long> myRating = new ArrayList<>() ;
    public static int selectedAddress ;
    public static List<AddressesModel> addressesModelList = new ArrayList<>() ;
    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>() ;
    public static List<NotificationModel> notificationModelList = new ArrayList<>() ;
    private static ListenerRegistration registration ;

    public static void loadCategories(final RecyclerView categoryRecyclerview, final Context context) {
        categoryModelList.clear() ;
        CITY_FIREBASE_FIRESTORE.collection(STORE_CATEGORIES).orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerview.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerview, final Context context , final int index, final String categoryName) {
        CITY_FIREBASE_FIRESTORE.collection(STORE_CATEGORIES).document(categoryName.toUpperCase()).collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((long)documentSnapshot.get("view_type") == 0 && documentSnapshot.getLong("no_of_banners") != 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>() ;
                                    long no_of_banners = (long)documentSnapshot.get("no_of_banners") ;
                                    for (long x = 1 ; x <= no_of_banners ; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_"+x).toString() , documentSnapshot.get("banner_"+x+"_background").toString())) ;
                                    }
                                    lists.get(index).add(new HomePageModel(0 , sliderModelList)) ;
                                    //break;
                                }else if ((long)documentSnapshot.get("view_type") == 1 && documentSnapshot.get("strip_ad_banner").toString() != "") {
                                    lists.get(index).add(new HomePageModel(1 , documentSnapshot.get("strip_ad_banner").toString() , documentSnapshot.get("background").toString())) ;
                                    //break;
                                }else if ((long)documentSnapshot.get("view_type") == 2 && documentSnapshot.getLong("no_of_products") != 0) {
                                    final List<WishlistModel> viewAllProductList = new ArrayList<>() ;
                                    final List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(
                                                new HorizontalProductScrollModel
                                                        (documentSnapshot.get("product_ID_" + x).toString(),
                                                                documentSnapshot.get("product_image_" + x).toString(),
                                                                documentSnapshot.get("product_title_" + x).toString(),
                                                                documentSnapshot.get("product_subtitle_" + x).toString(),
                                                                documentSnapshot.get("product_price_" + x).toString()));
                                        viewAllProductList.add(
                                                new WishlistModel(documentSnapshot.get("product_ID_"+x).toString()
                                                        ,documentSnapshot.get("product_image_"+x).toString()
                                                        ,documentSnapshot.get("product_full_title_" + x).toString()
                                                        ,documentSnapshot.get("average_rating_" + x).toString()
                                                        ,documentSnapshot.get("product_price_" + x).toString()
                                                        ,documentSnapshot.get("prev_price_" + x).toString()
                                                        ,(boolean)documentSnapshot.get("COD_" + x)
                                                        ,(boolean)documentSnapshot.get("in_stock_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(2 , documentSnapshot.get("layout_title").toString() , documentSnapshot.get("layout_background").toString() , horizontalProductScrollModelList , viewAllProductList)) ;
                                    //lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList,viewAllProductList));
                                }else if ((long)documentSnapshot.get("view_type") == 3 && documentSnapshot.getLong("no_of_products") != 0) {
                                    final List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        gridLayoutModelList.add(
                                                new HorizontalProductScrollModel
                                                        (documentSnapshot.get("product_ID_" + x).toString(),
                                                                documentSnapshot.get("product_image_" + x).toString(),
                                                                documentSnapshot.get("product_title_" + x).toString(),
                                                                documentSnapshot.get("product_subtitle_" + x).toString(),
                                                                documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridLayoutModelList));
                                }
                            }
                            HomePageAdapter adapter;
                            if (categoryName.toUpperCase().equals("HOME")) {
                                adapter = new HomePageAdapter(lists.get(index) , true);
                            } else {
                                adapter = new HomePageAdapter(lists.get(index));
                            }
                            homePageRecyclerview.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishlist(final Context context , final Dialog dialog , final boolean loadProductData) {
        wishlist.clear();
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0 ; x < (long)task.getResult().get("list_size") ; x++) {
                        wishlist.add(task.getResult().get("product_ID_"+x).toString()) ;

                        if (wishlist.contains(ProductDetailsActivity.productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true ;
                            if (ProductDetailsActivity.addToWishlistButton != null)
                                ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(context.getResources().getColorStateList(R.color.buttonRed));
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false ;
                            if (ProductDetailsActivity.addToWishlistButton != null)
                                ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        }

                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String productID = task.getResult().get("product_ID_" + x).toString() ;
                            CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot = task.getResult() ;
                                        CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY").orderBy("time" , Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")) {
                                                                wishlistModelList.add(
                                                                        new WishlistModel(productID
                                                                                , documentSnapshot.get("product_image_1").toString()
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("average_rating").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , documentSnapshot.get("prev_price").toString()
                                                                                , (boolean) documentSnapshot.get("COD")
                                                                                , true));
                                                            }else {
                                                                wishlistModelList.add(
                                                                        new WishlistModel(productID
                                                                                , documentSnapshot.get("product_image_1").toString()
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("average_rating").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , documentSnapshot.get("prev_price").toString()
                                                                                , (boolean) documentSnapshot.get("COD")
                                                                                , false));
                                                            }
                                                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged() ;
                                                        }else {
                                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }) ;
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss() ;
            }
        }) ;
    }

    public static void removeFromWishlist(final int index , final Context context) {
        final String removedProductID = wishlist.get(index) ;
        wishlist.remove(index) ;
        Map<String , Object> updateWishlist = new HashMap<>() ;
        for (int x = 0 ; x < wishlist.size() ; x++) {
            updateWishlist.put("product_ID_"+x , wishlist.get(x)) ;
        }
        updateWishlist.put("list_size" , (long)wishlist.size()) ;
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (wishlistModelList.size() != 0) {
                        wishlistModelList.remove(index) ;
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false ;
                    Toast.makeText(context, "Product removed", Toast.LENGTH_SHORT).show();
                } else {
                    if (ProductDetailsActivity.addToWishlistButton != null)
                        ProductDetailsActivity.addToWishlistButton.setSupportImageTintList(context.getResources().getColorStateList(R.color.buttonRed));
                    wishlist.add(removedProductID) ;
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (ProductDetailsActivity.addToWishlistButton != null)
                    ProductDetailsActivity.running_wishlist_query = false ;
                //addToWishlistButton.setEnabled(true) ;
            }
        }) ;
    }

    public static void loadRatingList(final Context context) {
        myRatedId.clear();
        myRating.clear();
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_RATINGS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> orderedProductIds = new ArrayList<>() ;
                    for (int x = 0 ; x < myOrderItemModelList.size() ; x++) {
                        orderedProductIds.add(myOrderItemModelList.get(x).getProductId()) ;
                    }
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        myRatedId.add(task.getResult().get("product_ID_" + x).toString());
                        myRating.add((long) task.getResult().get("rating_" + x));

                        if (orderedProductIds.contains(task.getResult().get("product_ID_" + x).toString())){
                            myOrderItemModelList.get(orderedProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating((Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1));
                        }
                        if (MyOrdersFragment.myOrderAdapter != null) {
                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void loadCartList (final Context context , final Dialog dialog , final boolean loadProductData , final TextView badgeCount , final TextView cartTotalAmount) {
        cartList.clear();
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_CARTLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0 ; x < (long)task.getResult().get("list_size") ; x++) {
                        cartList.add(task.getResult().get("product_ID_"+x).toString()) ;

                        if (cartList.contains(ProductDetailsActivity.productID)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true ;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false ;

                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productID = task.getResult().get("product_ID_" + x).toString() ;
                            final long finalX = x;
                            CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult() ;
                                        CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY").orderBy("time" , Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            int index = 0 ;
                                                            if (cartList.size() >= 2) {
                                                                index = cartList.size() - 2 ;
                                                            }
                                                            //if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")) {
                                                            cartItemModelList.add(0 ,
                                                                    new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                            ,CartItemModel.CART_ITEM
                                                                            ,productID
                                                                            ,documentSnapshot.get("product_image_1").toString()
                                                                            , documentSnapshot.get("product_title").toString()
                                                                            , documentSnapshot.get("product_price").toString()
                                                                            , documentSnapshot.get("prev_price").toString()
                                                                            , (long)1
                                                                            , (long)documentSnapshot.get("max_quantity")
                                                                            , (long)documentSnapshot.get("stock_quantity")
                                                                            , true)) ;
                                                            if (finalX == 0 ) {
                                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT)) ;
                                                                LinearLayout parent = (LinearLayout)cartTotalAmount.getParent().getParent() ;
                                                                parent.setVisibility(View.VISIBLE);
                                                            }
                                                            if (cartList.size() == 0) {
                                                                cartItemModelList.clear();
                                                            }
                                                            MyCartFragment.cartAdapter.notifyDataSetChanged() ;
                                                        }else {
                                                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }) ;
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE) ;
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE) ;
                    }
                    if (cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }
                    else {
                        badgeCount.setText("++") ;
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss() ;
            }
        }) ;
    }

    public static void removeFromCart (final int index , final Context context , final TextView cartTotalAmount) {
        final String removedProductID = cartList.get(index) ;
        cartList.remove(index) ;
        Map<String , Object> updateCartList = new HashMap<>() ;
        for (int x = 0 ; x < cartList.size() ; x++) {
            updateCartList.put("product_ID_"+x , cartList.get(x)) ;
        }
        updateCartList.put("list_size" , (long)cartList.size()) ;
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_CARTLIST")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index) ;
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout)cartTotalAmount.getParent().getParent() ;
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                    Toast.makeText(context, "Product removed", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index , removedProductID) ;
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false ;
                //addToWishlistButton.setEnabled(true) ;
            }
        }) ;
    }

    public static void loadAddresses (final Context context , final Dialog loadingDialog , final boolean gotoDeliveryActivity) {
        addressesModelList.clear();
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Intent deliveryIntent;
                    if ((long) task.getResult().get("list_size") == 0) {
                        deliveryIntent = new Intent(context, AddAddressesActivity.class);
                        deliveryIntent.putExtra("INTENT", "deliveryIntent");
                    } else {
                        int count = 1;
                        long x;
                        for (x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            if (task.getResult().getString("city_" + x).equals(CITY)) {
                                addressesModelList.add(new AddressesModel(
                                        (boolean) task.getResult().get("selected_" + x),
                                        task.getResult().getString("locality_" + x),
                                        task.getResult().getString("flat_no_" + x),
                                        task.getResult().getString("landmark_" + x),
                                        task.getResult().getString("name_" + x),
                                        task.getResult().getString("mobile_no_" + x),
                                        task.getResult().getString("alternate_mobile_no_" + x)));
                                if ((boolean) task.getResult().get("selected_" + x)) {
                                    selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                                }
                            } else {
                                count++;
                            }
                        }
                        if (count == x) {
                            deliveryIntent = new Intent(context, AddAddressesActivity.class);
                            deliveryIntent.putExtra("INTENT", "deliveryIntent");
                        } else{
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        }) ;
    }

    public static void loadOrders(final Context context , @Nullable final MyOrderAdapter myOrderAdapter , @Nullable final Dialog loadingDialog) {
        myOrderItemModelList.clear();
        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_ORDERS).orderBy("time" , Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            MainActivity.ordered = true;
                            for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                                CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(documentSnapshot.getString("Order_id")).collection("OrderItems").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (DocumentSnapshot orderItems : task.getResult().getDocuments()){
                                                        MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("Product_Id")
                                                                , orderItems.getString("Product_Image")
                                                                , orderItems.getString("Product_Title")
                                                                , orderItems.getString("Order_Status")
                                                                , orderItems.getString("Address")
                                                                , orderItems.getString("Prev_Price")
                                                                , orderItems.getDate("Ordered Date")
                                                                , orderItems.getDate("Packed Date")
                                                                , orderItems.getDate("Delivered Date")
                                                                , orderItems.getDate("Cancelled Date")
                                                                , orderItems.getString("Discounted_Price")
                                                                , orderItems.getString("FullName")
                                                                , orderItems.getString("ORDER_ID")
                                                                , orderItems.getString("Payment_Method")
                                                                , orderItems.getString("Product_Price")
                                                                , orderItems.getLong("Product_Quantity")
                                                                , orderItems.getString("USER_ID")
                                                                , orderItems.getString("Delivery_Price")
                                                                , orderItems.getBoolean("Cancellation_Requested")) ;
                                                        myOrderItemModelList.add(myOrderItemModel) ;
                                                    }
                                                    loadRatingList(context);
                                                    if (myOrderAdapter != null) {
                                                        myOrderAdapter.notifyDataSetChanged();
                                                    }
                                                }else {
                                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                                if (loadingDialog != null) {
                                                    loadingDialog.dismiss();
                                                }
                                            }
                                        }) ;
                            }
                        }else {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }) ;
    }

    public static void checkNotifications(boolean remove , @Nullable final TextView notifyCount) {
        if (remove) {
            if (CURR_USER != null)
                registration.remove();
        }else {
            registration = USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_NOTIFICATIONS")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                notificationModelList.clear();
                                int unRead = 0 ;
                                for (long x = 0 ; x < (long)documentSnapshot.get("list_size") ; x++) {
                                    notificationModelList.add(0 , new NotificationModel(documentSnapshot.getString("Image_" + x) , documentSnapshot.getString("Body_" + x) , documentSnapshot.getBoolean("Read_" + x))) ;
                                    if (!documentSnapshot.getBoolean("Read_" + x)) {
                                        unRead++ ;
                                        if (notifyCount != null) {
                                            if (unRead != 0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unRead < 99) {
                                                    notifyCount.setText(String.valueOf(unRead));
                                                } else {
                                                    notifyCount.setText("++");
                                                }
                                            } else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                                if (NotificationActivity.notificationAdapter != null) {
                                    NotificationActivity.notificationAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }) ;
        }
    }

    public static void clearData() {
        categoryModelList.clear() ;
        lists.clear() ;
        loadCategoriesNames.clear();
        wishlist.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedId.clear();
        myRating.clear();
        addressesModelList.clear();
        myOrderItemModelList.clear();
    }
}
