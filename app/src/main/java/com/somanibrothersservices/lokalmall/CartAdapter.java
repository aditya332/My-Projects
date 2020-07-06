package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.MIN_AMOUNT;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_PRODUCTS;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastposition = -1 ;
    private TextView cartTotalAmount ;
    private boolean showDeleteButton ;
    private Button continueBtn;
    //private Dialog checkCouponPriceDialog ;

    public CartAdapter(List<CartItemModel> cartItemModelList , TextView cartTotalAmount , boolean showDeleteButton , Button continueBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount ;
        this.showDeleteButton = showDeleteButton ;
        this.continueBtn = continueBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout, viewGroup, false);
                return new CartTotalAmountViewholder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID() ;
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String prevPrice = cartItemModelList.get(position).getPrevPrice();
                Boolean inStock = cartItemModelList.get(position).getInStock() ;
                Long productQty = cartItemModelList.get(position).getProductQuantity() ;
                Long maxQty = cartItemModelList.get(position).getMaxQuantity() ;
                boolean qtyError = cartItemModelList.get(position).isQtyError() ;
                List<String> qtyIDs = cartItemModelList.get(position).getQtyIDs() ;
                long stockQty = cartItemModelList.get(position).getStockQuantity() ;
                boolean cod = cartItemModelList.get(position).isCod() ;
                ((CartItemViewHolder)viewHolder).setItemDetails(productID , resource,title,productPrice,prevPrice, position , inStock , String.valueOf(productQty) , maxQty , qtyError , qtyIDs , stockQty , cod);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0 ;
                int totalItemPrice = 0 ;
                int totalAmount ;
                int savedAmount = 0 ;
                for (int x = 0 ; x < cartItemModelList.size() ; x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM ) {
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity())) ;
                        totalItems += quantity ;
                        totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * quantity;
                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getPrevPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getPrevPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity ;
                        }else {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity ;
                        }
                    }
                }
                totalAmount = totalItemPrice ;
                cartItemModelList.get(position).setTotalItems(totalItems); ;
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewholder)viewHolder).setTotalAmount(totalItems,totalItemPrice,totalAmount,savedAmount);
                break;
            default:
                return;
        }
        if (lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView prevPrice;
        private TextView productQuantity;
        private LinearLayout deleteButton ;
        private TextView discountedPrice , originalPrice ;
        private String productOrigignalPrice ;
        private ImageView codIndicator ;


        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            prevPrice = itemView.findViewById(R.id.prev_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            deleteButton = itemView.findViewById(R.id.remove_item_btn) ;
            codIndicator = itemView.findViewById(R.id.cod_indicator) ;
        }

        private void setItemDetails(final String productID , final String resource, String title , final String productPriceText, final String prevPriceText, final int position , final boolean inStock , final String productQty , final Long maxQty , boolean qtyError , final List<String> qtyIDs , final long stockQty , boolean cod) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(productImage);
            //productImage.setImageResource(resource);
            productTitle.setText(title);

            final Dialog checkCouponPriceDialog = new Dialog(itemView.getContext());
            checkCouponPriceDialog.setCancelable(false);
            checkCouponPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (cod) {
                codIndicator.setVisibility(View.VISIBLE);
            } else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            //if (inStock) {
            productPrice.setText(" ₹ " + productPriceText);
            productPrice.setTextColor(Color.parseColor("#000000"));
            prevPrice.setText(" ₹ " + prevPriceText);

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            productOrigignalPrice = prevPriceText;

            //discountedPrice.setText(" ₹ " + cartItemModelList.get(position).getDiscountedPrice());
            //DBqueries.cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3));
            productPrice.setText(" ₹ " + cartItemModelList.get(position).getProductPrice());
            //String offerDiscountAmount = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));

            productQuantity.setVisibility(View.VISIBLE);

            productQuantity.setText("Qty:- " + productQty);
            if (!showDeleteButton) {
                if (qtyError) {
                    productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                    productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                } else {
                    productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                    productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                }
            }
            productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.quantity_dialog);
                    quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    quantityDialog.setCancelable(false);
                    final EditText quantityNo = quantityDialog.findViewById(R.id.quantiy_no);
                    Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                    Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                    quantityNo.setHint("MAX " + String.valueOf(maxQty));
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quantityDialog.dismiss();
                        }
                    });
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(quantityNo.getText())) {
                                if (Long.valueOf(quantityNo.getText().toString()) <= maxQty && Long.parseLong(quantityNo.getText().toString()) != 0) {
                                    if (itemView.getContext() instanceof MainActivity) {
                                        cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                    } else {
                                        if (DeliveryActivity.fromCart) {
                                            cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                        } else {
                                            DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                        }
                                    }
                                    productQuantity.setText("Qty: " + quantityNo.getText());
                                    notifyItemChanged(cartItemModelList.size() - 1);
                                    if (!showDeleteButton) {
                                        DeliveryActivity.loadingDialog.show();
                                        DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                        final int initialQty = Integer.parseInt(productQty);
                                        final int finalQty = Integer.parseInt(quantityNo.getText().toString());
                                        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        //int qtyDiffrence
                                        if (finalQty > initialQty) {
                                            for (int y = 0; y < (finalQty - initialQty); y++) {
                                                final String qtyDocumentName = UUID.randomUUID().toString().substring(0, 20);
                                                Map<String, Object> timeStamp = new HashMap<>();
                                                timeStamp.put("time", FieldValue.serverTimestamp());
                                                final int finalY = y;
                                                CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY").document(qtyDocumentName)
                                                        .set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        qtyIDs.add(qtyDocumentName);
                                                        if ((finalY + 1) == (finalQty - initialQty)) {
                                                            CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                                    .limit(stockQty).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        List<String> serverQty = new ArrayList<>();
                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                            serverQty.add(queryDocumentSnapshot.getId());
                                                                        }
                                                                        long availableQty = 0;
                                                                        for (String qtyID : qtyIDs) {
                                                                            if (!serverQty.contains(qtyID)) {
                                                                                DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                Toast.makeText(itemView.getContext(), "Required Quantity may not be available", Toast.LENGTH_SHORT).show();
                                                                                //DeliveryActivity.allProductsAvailable = false;
                                                                            } else {
                                                                                availableQty++;
                                                                            }
                                                                        }
                                                                        DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    } else {
                                                                        Toast.makeText(itemView.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    DeliveryActivity.loadingDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        } else if (initialQty > finalQty) {
                                            for (int x = 0; x < (initialQty - finalQty); x++) {
                                                final String qtyID = qtyIDs.get(qtyIDs.size() - 1 - x);
                                                final int finalX = x;
                                                CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document(productID).collection("QUANTITY")
                                                        .document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        qtyIDs.remove(qtyID);
                                                        DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                        if (finalX + 1 == (initialQty - finalQty)) {
                                                            DeliveryActivity.loadingDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(itemView.getContext(), "Max Quantity " + maxQty, Toast.LENGTH_SHORT).show();
                                }
                            }
                            quantityDialog.dismiss();
                        }
                    });
                    quantityDialog.show();
                }
            });
            if (showDeleteButton) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBqueries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }
                }
            });
        }
    }

    class CartTotalAmountViewholder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmountViewholder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemsText, int totalItemsPriceText, int totalAmountText, int savedAmountText) {

            if (totalAmountText <= Integer.parseInt(MIN_AMOUNT)) {
                continueBtn.setEnabled(false);
                continueBtn.setText("Total Amount has to be greater than ₹ " + MIN_AMOUNT );
                continueBtn.setAllCaps(false);
            } else {
                continueBtn.setEnabled(true);
                continueBtn.setText("CONTINUE");
            }

            totalItems.setText("Price( " + totalItemsText + " items )");
            totalItemPrice.setText("₹ " + totalItemsPriceText);
            totalAmount.setText("₹ " + totalAmountText);
            cartTotalAmount.setText("₹ " + totalAmountText);
            savedAmount.setText("You saved ₹ " + savedAmountText + " on this order");

            LinearLayout parent = (LinearLayout)cartTotalAmount.getParent().getParent() ;
            if (totalItemsPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                if (showDeleteButton){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE) ;
            }else {
                parent.setVisibility(View.VISIBLE) ;
            }
        }
    }
}
