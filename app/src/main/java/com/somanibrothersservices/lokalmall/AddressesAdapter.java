package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {
    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        this.preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog = loadingDialog;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addresses_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String name = addressesModelList.get(position).getName();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        String alternateMobileNo = addressesModelList.get(position).getAlternateMobileNo();
        String flatNo = addressesModelList.get(position).getFlatNo();
        String locality = addressesModelList.get(position).getLocality();
        String landmark = addressesModelList.get(position).getLandmark();
        boolean selected = addressesModelList.get(position).getSelected();
        viewHolder.setData(name, mobileNo, alternateMobileNo, flatNo, locality, landmark,selected, position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView address;
        private LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String userName, String mobileNo, String alternateMobileNo, String flatNo, String locality, String landmark,final Boolean selected, final int position) {
            if (alternateMobileNo.equals("")) {
                name.setText(userName + " - " + mobileNo);
            } else {
                name.setText(userName + " - " + mobileNo + " or " + alternateMobileNo);
            }
            if (landmark.equals("")) {
                address.setText(flatNo + " , " + locality);
            } else {
                address.setText(flatNo + " , " + locality + " , " + landmark);
            }
            if (MODE == DeliveryActivity.SELECT_ADDRESS) {
                icon.setImageResource(R.drawable.ic_check_blue_24dp);
                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            MyAddressesActivity.refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });
            } else if (MODE == MyAccountFragment.MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addAddressIntent = new Intent(itemView.getContext(), AddAddressesActivity.class);
                        addAddressIntent.putExtra("INTENT", "update_address");
                        addAddressIntent.putExtra("index", position);
                        itemView.getContext().startActivity(addAddressIntent);
                        refresh = false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(itemView.getContext(), "This feature is currently disabled ", Toast.LENGTH_SHORT).show();
                        /*loadingDialog.show();
                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for (int i = 0; i < addressesModelList.size(); i++) {
                            if (i != position) {
                                x++;
                                addresses.put("locality_" + x, FieldValue.delete());
                                addresses.put("flat_no_" + x, FieldValue.delete());
                                addresses.put("landmark_" + x, FieldValue.delete());
                                addresses.put("name_" + x, FieldValue.delete());
                                addresses.put("mobile_no_" + x, FieldValue.delete());
                                addresses.put("alternate_mobile_no_" + x, FieldValue.delete());
                                if (addressesModelList.get(position).getSelected()) {
                                    if (position - 1 >= 0) {
                                        if (x == position) {
                                            addresses.put("selected_" + x, FieldValue.delete());
                                            selected = x;
                                        }else {
                                            addresses.put("selected_" + x, FieldValue.delete());
                                        }
                                    } else {
                                        if (x == 1) {
                                            addresses.put("selected_" + x, FieldValue.delete());
                                            selected = x;
                                        }else {
                                            addresses.put("selected_" + x, FieldValue.delete());
                                        }
                                    }
                                } else {
                                    addresses.put("selected_" + x, FieldValue.delete());
                                    if (addressesModelList.get(i).getSelected()) {
                                        selected = x ;
                                    }
                                }
                            }
                        }
                        addresses.put("list_size", x);
                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(CURR_USER.getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .update(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DBqueries.addressesModelList.remove(position);
                                    if (finalSelected != -1) {
                                        DBqueries.selectedAddress = finalSelected - 1;
                                        DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                    }else if (DBqueries.addressesModelList.size() == 0) {
                                        DBqueries.selectedAddress = - 1 ;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(itemView.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
                        refresh = false;*/
                    }
                });
                icon.setImageResource(R.drawable.ic_more_vert_black_24dp);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            MyAddressesActivity.refreshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh = true;
                        }
                        preSelectedPosition = position;

                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyAddressesActivity.refreshItem(preSelectedPosition, preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }

}
