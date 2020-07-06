package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_ORDERS;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position, rating;
    private TextView title, price, qty;
    private ImageView productImage, orderedIndicator, packedIndicator,deliveredIndicator;
    private ProgressBar o_p_progress, p_d_progress;
    private TextView orderedTitle,  packedTitle, deliveredTitle, orderedDate, packedDate, deliveredDate, orderedBody,packedBody, deliveredBody;
    private TextView fullName, address;
    private TextView totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount;
    private Dialog loadingDialog, cancelDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();

        cancelDialog = new Dialog(this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        //cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT) ;
        //loadingDialog.show();

        position = getIntent().getIntExtra("position", -1);
        final MyOrderItemModel model = DBqueries.myOrderItemModelList.get(position);

        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        qty = findViewById(R.id.product_quantity);
        productImage = findViewById(R.id.product_image);
        orderedIndicator = findViewById(R.id.ordered_indicator);
        orderedTitle = findViewById(R.id.ordered_title);
        orderedDate = findViewById(R.id.ordered_date);
        orderedBody = findViewById(R.id.ordered_body);
        o_p_progress = findViewById(R.id.ordered_packed_progress);
        packedIndicator = findViewById(R.id.packed_indicator);
        packedTitle = findViewById(R.id.packed_title);
        packedDate = findViewById(R.id.packed_date);
        packedBody = findViewById(R.id.packed_body);
        p_d_progress = findViewById(R.id.packed_delivered_progress);
        deliveredIndicator = findViewById(R.id.delivered_indicator);
        deliveredTitle = findViewById(R.id.delivered_title);
        deliveredDate = findViewById(R.id.delivered_date);
        deliveredBody = findViewById(R.id.delivered_body);
        cancelOrderButton = findViewById(R.id.cancel_button);

        fullName = findViewById(R.id.full_name);
        address = findViewById(R.id.address);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        totalAmount = findViewById(R.id.total_price);
        savedAmount = findViewById(R.id.saved_amount);

        title.setText(model.getProductTitle());
        if (!model.getDiscountedPrice().equals("")) {
            price.setText(" ₹ " + model.getDiscountedPrice());
        } else {
            price.setText(" ₹ " + model.getProductPrice());
        }
        qty.setText("Qty : " + String.valueOf(model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                o_p_progress.setVisibility(View.GONE);
                p_d_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                o_p_progress.setProgress(100);
                o_p_progress.setVisibility(View.VISIBLE);
                p_d_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                o_p_progress.setVisibility(View.VISIBLE);
                p_d_progress.setProgress(100);
                p_d_progress.setVisibility(View.VISIBLE);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery .");

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                deliveredIndicator.setVisibility(View.VISIBLE);
                deliveredBody.setVisibility(View.VISIBLE);
                deliveredDate.setVisibility(View.VISIBLE);
                deliveredTitle.setVisibility(View.VISIBLE);

                break;

            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                o_p_progress.setVisibility(View.VISIBLE);
                p_d_progress.setProgress(100);
                p_d_progress.setVisibility(View.VISIBLE);

                packedIndicator.setVisibility(View.VISIBLE);
                packedBody.setVisibility(View.VISIBLE);
                packedDate.setVisibility(View.VISIBLE);
                packedTitle.setVisibility(View.VISIBLE);

                deliveredIndicator.setVisibility(View.VISIBLE);
                deliveredBody.setVisibility(View.VISIBLE);
                deliveredDate.setVisibility(View.VISIBLE);
                deliveredTitle.setVisibility(View.VISIBLE);

                break;

            case "Cancelled":
                if (model.getPackedDate().after(model.getOrderedDate())) {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                    o_p_progress.setProgress(100);
                    o_p_progress.setVisibility(View.VISIBLE);
                    p_d_progress.setProgress(100);
                    p_d_progress.setVisibility(View.GONE);

                    packedIndicator.setVisibility(View.VISIBLE);
                    packedBody.setVisibility(View.VISIBLE);
                    packedDate.setVisibility(View.VISIBLE);
                    packedTitle.setVisibility(View.VISIBLE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled");

                    o_p_progress.setProgress(100);
                    o_p_progress.setVisibility(View.VISIBLE);
                    p_d_progress.setVisibility(View.GONE);

                    packedIndicator.setVisibility(View.VISIBLE);
                    packedBody.setVisibility(View.VISIBLE);
                    packedDate.setVisibility(View.VISIBLE);
                    packedTitle.setVisibility(View.VISIBLE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }

        }

        if (model.isCancellationRequested()) {
            cancelOrderButton.setEnabled(false);
            cancelOrderButton.setText("Cancellation in process");
            cancelOrderButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        } else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")) {
                cancelOrderButton.setVisibility(View.VISIBLE);
                cancelOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                final Map<String, Object> map = new HashMap<>();
                                map.put("Order_Id", model.getOrderId());
                                map.put("Product_Id", model.getProductId());
                                map.put("Order_Cancelled", false);
                                CITY_FIREBASE_FIRESTORE.collection(STORE).document(STORE).collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String , Object> cancelOrder = new HashMap<>();
                                                    cancelOrder.put("Cancellation_Requested", true);
                                                    cancelOrder.put("Order_Status" , "cancelled");
                                                    CITY_FIREBASE_FIRESTORE.collection(STORE_ORDERS).document(model.getOrderId()).collection("OrderItems").document(model.getProductId())
                                                            .update(cancelOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                model.setCancellationRequested(true);
                                                                model.setOrderStatus("Cancelled");
                                                                cancelOrderButton.setEnabled(false);
                                                                cancelOrderButton.setText("Cancellation in process");
                                                                cancelOrderButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                cancelOrderButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                            } else {
                                                                Toast.makeText(OrderDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    loadingDialog.dismiss();
                                                    Toast.makeText(OrderDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }
        fullName.setText(model.getFullName());
        address.setText(model.getAddress());

        totalItems.setText("Price( " + model.getProductQuantity() + " items )");
        Long totalItemsPriceValue;
        if (model.getDiscountedPrice().equals("")) {
            totalItemsPriceValue = model.getProductQuantity() * Long.parseLong(model.getProductPrice());
            totalItemsPrice.setText(" ₹ " + totalItemsPriceValue);
        } else {
            totalItemsPriceValue = model.getProductQuantity() * Long.parseLong(model.getDiscountedPrice());
            totalItemsPrice.setText(" ₹ " + totalItemsPriceValue);
        }
        totalAmount.setText(totalItemsPrice.getText());

        if (!model.getPrevPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved ₹ " + model.getProductQuantity() * (Long.valueOf(model.getPrevPrice()) - Long.valueOf(model.getDiscountedPrice())) + " on this order");
            } else {
                savedAmount.setText("You saved ₹ " + model.getProductQuantity() * (Long.valueOf(model.getPrevPrice()) - Long.valueOf(model.getProductPrice())) + " on this order");
            }
        } else {
            if (model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved ₹ 0 on this order");
            } else {
                savedAmount.setText("You saved ₹ " + model.getProductQuantity() * (Long.valueOf(model.getProductPrice()) - Long.valueOf(model.getDiscountedPrice())) + " on this order");
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
