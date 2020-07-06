package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.ACCEPTS_UPI;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_ORDERS;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_PRODUCTS;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_UPI_ID;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_ORDERS;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class DeliveryActivity extends AppCompatActivity implements PaymentStatusListener  {

    public static List<CartItemModel> cartItemModelList;
    public static boolean fromCart, getQtyIDs = true;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddNewAddressBtn, continueButton;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount, fullName, fullAddress, orderId;
    private String name, mobileNo, approvalRefNo;
    private Dialog paymentMethodDialog;
    public static Dialog loadingDialog;
    private TextView codButtonTitle;
    private View divider;
    private ImageButton upi, cod, continueShoppingButton;
    final int UPI_PAYMENT = 0;
    private String paymentMethod = "COD";
    private ConstraintLayout orderConfirmationLayout;
    private boolean successResponse = false, firstTime;
    //public static boolean allProductsAvailable ;
    public static CartAdapter cartAdapter;
    private EasyUpiPayment mEasyUpiPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();
        paymentMethodDialog = new Dialog(this);
        paymentMethodDialog.setContentView(R.layout.payment_method_dialog);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //paymentMethodDialog.show();

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullName = findViewById(R.id.full_name);
        fullAddress = findViewById(R.id.address);
        continueButton = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingButton = findViewById(R.id.continue_shopping_button);
        orderId = findViewById(R.id.order_id);

        getQtyIDs = true;
        firstTime = true;
        //allProductsAvailable = true ;
        approvalRefNo = UUID.randomUUID().toString().substring(0, 15);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false, continueButton);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });


        upi = paymentMethodDialog.findViewById(R.id.upi_button);
        cod = paymentMethodDialog.findViewById(R.id.cod_button);
        codButtonTitle = paymentMethodDialog.findViewById(R.id.cod_button_title);
        divider = paymentMethodDialog.findViewById(R.id.divider);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allProductAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductAvailable = false;
                        break;
                    }
                    if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCod()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codButtonTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codButtonTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (allProductAvailable) {
                    paymentMethodDialog.show();
                }
            }
        });
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent otpIntent = new Intent(DeliveryActivity.this , OTPverificationActivity.class) ;
                otpIntent.putExtra("mobileNo" , mobileNo.substring(0 , 10)) ;
                startActivity(otpIntent) ;*/
                paymentMethod = "COD";
                firstTime = true;
                placeOrderDetails();
                //cod();
            }
        });
        upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACCEPTS_UPI) {
                    paymentMethod = "UPI";
                    placeOrderDetails();
                } else {
                    Toast.makeText(DeliveryActivity.this, "Store is not accepting UPI Payment Currently , \nYou will have to opt for Cash On Delivery !!", Toast.LENGTH_LONG).show();
                }
                //upi();
            }
        });
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String qtyDocumentName = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyDocumentName)
                            .set(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                cartItemModelList.get(finalX).getQtyIDs().add(qtyDocumentName);
                                if ((finalY + 1) == cartItemModelList.get(finalX).getProductQuantity()) {
                                    CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                            .limit(cartItemModelList.get(finalX).getStockQuantity()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                List<String> serverQty = new ArrayList<>();
                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                    serverQty.add(queryDocumentSnapshot.getId());
                                                }
                                                long availableQty = 0;
                                                boolean noLongerAvailable = true;
                                                for (String qtyID : cartItemModelList.get(finalX).getQtyIDs()) {
                                                    if (!serverQty.contains(qtyID)) {
                                                        cartItemModelList.get(finalX).setInStock(false);
                                                        if (noLongerAvailable) {
                                                            cartItemModelList.get(finalX).setInStock(false);
                                                        } else {
                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                            Toast.makeText(DeliveryActivity.this, "Required Quantity may not be available", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        availableQty++;
                                                        noLongerAvailable = false;
                                                    }
                                                }
                                                cartAdapter.notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        } else {
            getQtyIDs = true;
        }

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        } else {
            fullName.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        if (landmark.equals("")) {
            fullAddress.setText(flatNo + " , " + locality);
        } else {
            fullAddress.setText(flatNo + " , " + locality + " , " + landmark);
        }
    }

    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                                .document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                    cartItemModelList.get(finalX).getQtyIDs().clear();
                                }
                            }
                        });
                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    private void showConfirmationLayout(final boolean isCOD) {
        successResponse = true;
        MainActivity.ordered = true;
        paymentMethodDialog.dismiss();
        loadingDialog.dismiss();
        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                        .document(qtyID).update("user_ID", CURR_USER.getUid());
            }
        }

        USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    List<String> ordered = new ArrayList<>();
                    ordered = (List<String>) task.getResult().get("ordered");
                    if (!ordered.contains(CITY + STORE)) {
                        ordered.add(CITY + STORE);
                    }
                    USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).update("ordered", ordered)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (MainActivity.mainActivity != null) {
                                            MainActivity.mainActivity.finish();
                                            MainActivity.mainActivity = null;
                                            MainActivity.showCart = false;
                                        } else {
                                            MainActivity.resetMainActivity = true;
                                        }
                                        if (ProductDetailsActivity.productDetailsActivity != null) {
                                            ProductDetailsActivity.productDetailsActivity.finish();
                                            ProductDetailsActivity.productDetailsActivity = null;
                                        }

                                        if (fromCart) {
                                            loadingDialog.show();
                                            Map<String, Object> updateCartList = new HashMap<>();
                                            long cartlistSize = 0;
                                            final List<Integer> indexList = new ArrayList<>();
                                            for (int x = 0; x < DBqueries.cartList.size(); x++) {
                                                if (!DBqueries.cartItemModelList.get(x).getInStock()) {
                                                    updateCartList.put("product_ID_" + cartlistSize, DBqueries.cartItemModelList.get(x).getProductID());
                                                    cartlistSize++;
                                                } else {
                                                    indexList.add(x);
                                                }
                                            }
                                            updateCartList.put("list_size", cartlistSize);

                                            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_CARTLIST")
                                                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        DBqueries.cartList.clear();
                                                        DBqueries.cartItemModelList.clear();
                                                    } else {
                                                        Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                        }
                                        TextView codAmount = findViewById(R.id.cod_amount);
                                        if (isCOD) {
                                            codAmount.setText("Amount to be paid :-  ₹ " + totalAmount.getText().toString().substring(2, totalAmount.length()));
                                        } else {
                                            codAmount.setText("Amount paid :-  ₹ " + totalAmount.getText().toString().substring(2, totalAmount.length()));
                                        }
                                        continueButton.setEnabled(false);
                                        changeOrAddNewAddressBtn.setEnabled(false);
                                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                        orderConfirmationLayout.setVisibility(View.VISIBLE);
                                        orderId.setText("Order id :- " + approvalRefNo);
                                        continueShoppingButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();
                                            }
                                        });
                                        DBqueries.loadOrders(DeliveryActivity.this, null, null);
                                    } else {
                                        Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void placeOrderDetails() {
        if (firstTime) {
            loadingDialog.show();
            String userID = FirebaseAuth.getInstance().getUid();
            for (CartItemModel cartItemModel : cartItemModelList) {
                if (cartItemModel.getType() == CartItemModel.CART_ITEM) {
                    Map<String, Object> orderDetails = new HashMap<>();
                    orderDetails.put("ORDER_ID", approvalRefNo);
                    orderDetails.put("Product_Id", cartItemModel.getProductID());
                    orderDetails.put("Product_Image", cartItemModel.getProductImage());
                    orderDetails.put("Product_Title", cartItemModel.getProductTitle());
                    orderDetails.put("USER_ID", userID);
                    orderDetails.put("Product_Quantity", cartItemModel.getProductQuantity());
                    if (cartItemModel.getPrevPrice() != null) {
                        orderDetails.put("Prev_Price", cartItemModel.getPrevPrice());
                    } else {
                        orderDetails.put("Prev_Price", "");
                    }
                    orderDetails.put("Product_Price", cartItemModel.getProductPrice());
                    if (cartItemModel.getDiscountedPrice() != null) {
                        orderDetails.put("Discounted_Price", cartItemModel.getDiscountedPrice());
                    } else {
                        orderDetails.put("Discounted_Price", "");
                    }
                    orderDetails.put("Ordered Date", FieldValue.serverTimestamp());
                    orderDetails.put("Packed Date", FieldValue.serverTimestamp());
                    orderDetails.put("Delivered Date", FieldValue.serverTimestamp());
                    orderDetails.put("Cancelled Date", FieldValue.serverTimestamp());
                    orderDetails.put("Order_Status", "Ordered");
                    orderDetails.put("Payment_Method", paymentMethod);
                    orderDetails.put("Address", fullAddress.getText());
                    orderDetails.put("FullName", fullName.getText());
                    orderDetails.put("Cancellation_Requested", false);
                    CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(approvalRefNo).collection("OrderItems").document(cartItemModel.getProductID())
                            .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Map<String, Object> orderDetails = new HashMap<>();
                    orderDetails.put("Ordered Date", FieldValue.serverTimestamp());
                    orderDetails.put("Total_Items", cartItemModel.getTotalItems());
                    orderDetails.put("Total_Items_price", cartItemModel.getTotalItemPrice());
                    orderDetails.put("Total_Amount", cartItemModel.getTotalAmount());
                    orderDetails.put("Saved_Amount", cartItemModel.getSavedAmount());
                    orderDetails.put("Payment_Status", "not paid");
                    orderDetails.put("Order_Status", "cancelled");
                    orderDetails.put("Address", fullAddress.getText());
                    orderDetails.put("FullName", fullName.getText());
                    orderDetails.put("Payment_Method", paymentMethod);
                    CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(approvalRefNo)
                            .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (paymentMethod.equals("UPI")) {
                                    upi();
                                } else {
                                    cod();
                                }
                            } else {
                                Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        } else {
            if (paymentMethod.equals("UPI")) {
                upi();
            } else {
                cod();
            }
        }
    }

    private void upi() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        //payUsingUpi("Store Try", "adityajsomani33@oksbi", "Payment for Order", totalAmount.getText().toString().substring(2, totalAmount.length()));
        mEasyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa(STORE_UPI_ID)
                .setPayeeName(STORE + " , " + CITY)
                .setTransactionId(approvalRefNo)
                .setTransactionRefId(approvalRefNo + "-" + approvalRefNo)
                .setDescription("For your Order")
                .setAmount(totalAmount.getText().toString().substring(2, totalAmount.length()) + ".00")
                .build();

        // Register Listener for Events
        mEasyUpiPayment.setPaymentStatusListener(this);

        mEasyUpiPayment.startPayment();
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString());
    }

    @Override
    public void onTransactionSuccess() {
        // Payment Success
        Toast.makeText(DeliveryActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();

        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("Payment_Status", "Paid");
        updateStatus.put("Order_Status", "Ordered");
        CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(approvalRefNo).update(updateStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userOrder = new HashMap<>();
                            userOrder.put("Order_id", approvalRefNo);
                            userOrder.put("time", FieldValue.serverTimestamp());
                            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_ORDERS).document(approvalRefNo)
                                    .set(userOrder)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showConfirmationLayout(false);
                                            } else {
                                                Toast.makeText(DeliveryActivity.this, "Failed to update user Order List", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            firstTime = false;
                            Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onTransactionSubmitted() {
        // Payment Pending
        Toast.makeText(this, "Pending | Submitted", Toast.LENGTH_SHORT).show();
        firstTime = false;
    }

    @Override
    public void onTransactionFailed() {
        // Payment Failed
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        firstTime = false;
    }

    @Override
    public void onTransactionCancelled() {
        // Payment Cancelled by User
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        firstTime = false;
    }

    @Override
    public void onAppNotFound() {
        Toast.makeText(this, "App Not Found", Toast.LENGTH_SHORT).show();
        firstTime = false;
    }
    private void cod() {
        getQtyIDs = false;
        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("Payment_Status", "Not Paid");
        updateStatus.put("Order_Status", "Ordered");
        CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(approvalRefNo).update(updateStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> userOrder = new HashMap<>();
                            userOrder.put("Order_id", approvalRefNo);
                            userOrder.put("time", FieldValue.serverTimestamp());
                            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_ORDERS).document(approvalRefNo)
                                    .set(userOrder)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                showConfirmationLayout(true);
                                            } else {
                                                Toast.makeText(DeliveryActivity.this, "Failed to update user Order List", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //Toast.makeText(DeliveryActivity.this, "Order Placed successfully!!!", Toast.LENGTH_SHORT).show();
    }
}
