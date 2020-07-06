package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class MyAddressesActivity extends AppCompatActivity {

    private int previousAddress ;
    private LinearLayout addNewAddressButton ;
    private RecyclerView myaddressesRecyclerView;
    private static AddressesAdapter addressesAdapter;
    private Button deliverHereBtn;
    private TextView addressesSaved ;
    private Dialog loadingDialog;
    private int mode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(MyAddressesActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size()) + " saved Addresses") ;
            }
        });

        myaddressesRecyclerView = findViewById(R.id.addresses_recyclerview);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddressButton = findViewById(R.id.add_new_address_btn) ;
        addressesSaved = findViewById(R.id.address_saved) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myaddressesRecyclerView.setLayoutManager(linearLayoutManager);
        previousAddress = DBqueries.selectedAddress ;


        mode = getIntent().getIntExtra("MODE",-1);
        if(mode == DeliveryActivity.SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBqueries.selectedAddress != previousAddress) {
                    final int previousAddressIndex = previousAddress ;
                    loadingDialog.show();
                    Map<String  , Object> updateSelection = new HashMap<>() ;
                    updateSelection.put("selected_" + String.valueOf(previousAddress + 1) , false) ;
                    updateSelection.put("selected_" + String.valueOf(DBqueries.selectedAddress + 1) , true) ;
                    previousAddress = DBqueries.selectedAddress ;
                    USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection("USER_DATA")
                            .document("MY_ADDRESSES").update(updateSelection)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                    }else {
                                        previousAddress = previousAddressIndex ;
                                        Toast.makeText(MyAddressesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            }) ;
                }
                else {
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBqueries.addressesModelList,mode , loadingDialog);
        myaddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myaddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        addNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(MyAddressesActivity.this , AddAddressesActivity.class) ;
                if (mode != DeliveryActivity.SELECT_ADDRESS) {
                    addAddressIntent.putExtra("INTENT" , "manage") ;
                }
                addAddressIntent.putExtra("INTENT" , "null") ;
                startActivity(addAddressIntent) ;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            if (mode == DeliveryActivity.SELECT_ADDRESS) {
                if (DBqueries.selectedAddress != previousAddress) {
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                    DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                    DBqueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void refreshItem(int deSelect, int select){
        addressesAdapter.notifyItemChanged(deSelect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public void onBackPressed() {
        if (mode == DeliveryActivity.SELECT_ADDRESS) {
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size()) + " saved Addresses") ;
    }
}