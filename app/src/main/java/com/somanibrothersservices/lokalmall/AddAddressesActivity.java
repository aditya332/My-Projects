package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class AddAddressesActivity extends AppCompatActivity {

    private Button saveBtn;
    private EditText locality, flatNo, name, landmark, mobileNo, alternateMobileNo;
    private Dialog loadingDialog;
    private boolean updateAddress = false ;
    private AddressesModel addressesModel ;
    private int position ;
    private CheckBox forMe;
    private Group groupForMe;
    private TextView city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_addresses);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add new Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saveBtn = findViewById(R.id.save_btn);

        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        city = findViewById(R.id.city);
        name = findViewById(R.id.name);
        landmark = findViewById(R.id.landmark);
        mobileNo = findViewById(R.id.mobile_no);
        alternateMobileNo = findViewById(R.id.alternate_mobile_no);
        forMe = findViewById(R.id.checkBoxForMe);
        groupForMe = findViewById(R.id.groupForMe);

        loadingDialog = new Dialog(AddAddressesActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();

        city.setText(CITY.substring(0,1).toUpperCase() + CITY.substring(1).toLowerCase());

        if (getIntent().getStringExtra("INTENT").equals("update_address")) {
            updateAddress = true ;
            position = getIntent().getIntExtra("index" , -1) ;
            addressesModel = DBqueries.addressesModelList.get(position) ;
            flatNo.setText(addressesModel.getFlatNo()) ;
            locality.setText(addressesModel.getLocality()) ;
            landmark.setText(addressesModel.getLandmark()) ;
            name.setText(addressesModel.getName()) ;
            mobileNo.setText(addressesModel.getMobileNo()) ;
            alternateMobileNo.setText(addressesModel.getAlternateMobileNo()) ;
            saveBtn.setText("Update") ;

        }else {
            position = DBqueries.addressesModelList.size();
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(locality.getText())) {
                    if (!TextUtils.isEmpty(flatNo.getText())) {
                        if (!TextUtils.isEmpty(name.getText()) || forMe.isChecked()) {
                            if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.length() == 10 || forMe.isChecked()) {
                                loadingDialog.show();
                                final String fullAddress = flatNo.getText().toString() + " " + locality.getText().toString();

                                Map<String, Object> addAddresses = new HashMap<>();
                                if (forMe.isChecked()) {
                                    addAddresses.put("mobile_no_" + String.valueOf(position + 1), DBqueries.mobileNo);
                                    addAddresses.put("alternate_mobile_no_" + String.valueOf(position + 1),"");
                                    addAddresses.put("name_" + String.valueOf(position + 1), DBqueries.fullName);
                                } else {
                                    addAddresses.put("mobile_no_" + String.valueOf(position + 1), mobileNo.getText().toString());
                                    addAddresses.put("alternate_mobile_no_" + String.valueOf(position + 1), alternateMobileNo.getText().toString());
                                    addAddresses.put("name_" + String.valueOf(position + 1), name.getText().toString());
                                }
                                addAddresses.put("locality_" + String.valueOf(position + 1), locality.getText().toString());
                                addAddresses.put("flat_no_" + String.valueOf(position + 1), flatNo.getText().toString());
                                addAddresses.put("landmark_" + String.valueOf(position + 1), landmark.getText().toString());
                                addAddresses.put("city_" + String.valueOf(position + 1),CITY);
                                if (!updateAddress) {
                                    addAddresses.put("list_size", position + 1);
                                    if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                        if (DBqueries.addressesModelList.size() == 0) {
                                            addAddresses.put("selected_" + String.valueOf(position + 1), true);
                                        }else {
                                            addAddresses.put("selected_" + String.valueOf(position + 1), false);
                                        }
                                    }else {
                                        addAddresses.put("selected_" + String.valueOf(position + 1), true);
                                    }
                                    if (DBqueries.addressesModelList.size() > 0) {
                                        addAddresses.put("selected_" + (DBqueries.selectedAddress + 1), false);
                                    }
                                }

                                USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid())
                                        .collection("USER_DATA").document("MY_ADDRESSES")
                                        .update(addAddresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (!updateAddress) {
                                                if (DBqueries.addressesModelList.size() > 0) {
                                                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                }
                                                if (forMe.isChecked()) {
                                                    DBqueries.addressesModelList.add(new AddressesModel(true, locality.getText().toString(), flatNo.getText().toString(), landmark.getText().toString(), DBqueries.fullName, DBqueries.mobileNo, ""));
                                                } else {
                                                    DBqueries.addressesModelList.add(new AddressesModel(true, locality.getText().toString(), flatNo.getText().toString(), landmark.getText().toString(), name.getText().toString(), mobileNo.getText().toString(), alternateMobileNo.getText().toString()));
                                                }
                                                if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                                    if (DBqueries.addressesModelList.size() == 0) {
                                                        DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                    }
                                                }else {
                                                    DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                }

                                            }else {
                                                DBqueries.addressesModelList.set(position , new AddressesModel(true , locality.getText().toString() , flatNo.getText().toString() , landmark.getText().toString() , name.getText().toString() , mobileNo.getText().toString() , alternateMobileNo.getText().toString() ));
                                            }
                                            if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                Intent deliveryIntent = new Intent(AddAddressesActivity.this, DeliveryActivity.class);
                                                startActivity(deliveryIntent);
                                            }else {
                                                MyAddressesActivity.refreshItem(DBqueries.selectedAddress , DBqueries.addressesModelList.size() - 1) ;
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(AddAddressesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                            } else {
                                mobileNo.requestFocus();
                            }
                        } else {
                            name.requestFocus();
                        }
                    } else {
                        flatNo.requestFocus();
                    }
                } else {
                    locality.requestFocus();
                }
            }
        });
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

    public void onClickForMe(View view) {
        if (forMe.isChecked()) {
            name.setEnabled(false);
            mobileNo.setEnabled(false);
            alternateMobileNo.setEnabled(false);
            name.setVisibility(View.INVISIBLE);
            mobileNo.setVisibility(View.INVISIBLE);
            alternateMobileNo.setVisibility(View.INVISIBLE);
        } else {
            name.setEnabled(true);
            mobileNo.setEnabled(true);
            alternateMobileNo.setEnabled(true);
            name.setVisibility(View.VISIBLE);
            mobileNo.setVisibility(View.VISIBLE);
            alternateMobileNo.setVisibility(View.VISIBLE);
        }
    }
}
