package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCartFragment extends Fragment {
    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private Button continueBtn;
    private TextView totalAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        totalAmount = view.findViewById(R.id.total_cart_amount);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(linearLayoutManager);


        //List<CartItemModel> cartItemModelList = new ArrayList<>();
        //cartItemModelList.add(new CartItemModel(1, "Price (3) items", "Rs.1198/-", "Free", "Rs.1198/-", "Rs. 230/-"));

        cartAdapter = new CartAdapter(DBqueries.cartItemModelList, totalAmount, true , continueBtn);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;
                for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
                    if (cartItemModel.getInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if (DBqueries.addressesModelList.size() == 0) {
                    DBqueries.loadAddresses(getContext(), loadingDialog, true);
                } else {
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.setPrimaryNavigationFragment(new HomeFragment());
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DBqueries.cartItemModelList.size() == 0) {
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        } else {
            LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT) {
                parent.setVisibility(View.INVISIBLE);
            }
            loadingDialog.dismiss();
        }
    }
}
