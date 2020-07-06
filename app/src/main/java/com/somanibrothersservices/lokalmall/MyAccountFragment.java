package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;

public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    public static final int MANAGE_ADDRESS = 1;
    private Button viewAllAddressBtn , signOutButton ;
    private CircleImageView profileView , currentOrderImage ;
    private TextView name , mobileNo , tvCurrentOrderStatus , yourRecentOrdersTitle , addressName , address ;
    private LinearLayout layoutContainer , yourRecentOrdersContainer ;
    private Dialog loadingDialog ;
    private ImageView orderIndicator , packedIndicator ,deliveredIndicator ;
    private ProgressBar o_p_progress , p_d_progress ;
    private FloatingActionButton settingsButton ;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_account, container, false);

        loadingDialog = new Dialog(getContext()) ;
        loadingDialog.setContentView(R.layout.loading_progress_dialog) ;
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background)) ;
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT) ;
        loadingDialog.show();

        viewAllAddressBtn = view.findViewById(R.id.view_all_addresses_btn);

        profileView = view.findViewById(R.id.profile_image) ;
        name = view.findViewById(R.id.user_name) ;
        mobileNo = view.findViewById(R.id.user_mobile) ;
        layoutContainer = view.findViewById(R.id.layout_container) ;
        currentOrderImage = view.findViewById(R.id.current_order_image) ;
        tvCurrentOrderStatus = view.findViewById(R.id.tv_current_order_status) ;
        orderIndicator = view.findViewById(R.id.ordered_indicator) ;
        packedIndicator = view.findViewById(R.id.packed_indicator) ;
        deliveredIndicator = view.findViewById(R.id.delivered_indicator) ;
        o_p_progress = view.findViewById(R.id.ordered_packed_progress) ;
        p_d_progress = view.findViewById(R.id.packed_delivered_progress) ;
        yourRecentOrdersTitle = view.findViewById(R.id.your_recent_orders_title) ;
        yourRecentOrdersContainer = view.findViewById(R.id.recent_orders_container) ;
        addressName = view.findViewById(R.id.address_full_name) ;
        address = view.findViewById(R.id.address) ;
        signOutButton = view.findViewById(R.id.sign_out_btn) ;
        settingsButton = view.findViewById(R.id.settings_btn) ;

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (MyOrderItemModel orderItemModel : DBqueries.myOrderItemModelList) {
                    if (!orderItemModel.isCancellationRequested()) {
                        if (!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(currentOrderImage) ;
                            tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());
                            switch (orderItemModel.getOrderStatus()) {
                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonRed)));
                                    packedIndicator.setVisibility(View.VISIBLE);
                                    o_p_progress.setProgress(50);
                                    o_p_progress.setVisibility(View.VISIBLE);
                                    break;

                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setVisibility(View.VISIBLE);
                                    o_p_progress.setProgress(100);
                                    o_p_progress.setVisibility(View.VISIBLE);
                                    break;

                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonRed)));
                                    packedIndicator.setVisibility(View.VISIBLE);
                                    deliveredIndicator.setVisibility(View.VISIBLE);
                                    o_p_progress.setProgress(100);
                                    o_p_progress.setVisibility(View.VISIBLE);
                                    p_d_progress.setProgress(50);
                                    p_d_progress.setVisibility(View.VISIBLE);
                                    break;

                                case "Delivered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setVisibility(View.VISIBLE);
                                    deliveredIndicator.setVisibility(View.VISIBLE);
                                    o_p_progress.setProgress(100);
                                    o_p_progress.setVisibility(View.VISIBLE);
                                    p_d_progress.setProgress(100);
                                    p_d_progress.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                }
                int i = 0 ;
                for (MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                    if ( i < 4) {
                        if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into((CircleImageView) yourRecentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    }else {
                        break;
                    }
                }
                if (i == 0) {
                    yourRecentOrdersTitle.setText("No Recent Orders .");
                }
                if (i < 3) {
                    for (int x = i  ; x < 4 ; x++) {
                        yourRecentOrdersContainer.getChildAt(x).setVisibility(View.GONE) ;
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBqueries.addressesModelList.size() == 0) {
                            addressName.setText("No Address");
                            address.setText("_");
                        }else {
                            setAddress() ;
                        }
                    }
                });
                DBqueries.loadAddresses(getContext() , loadingDialog , false);
            }
        });
        DBqueries.loadOrders(getContext() , null , loadingDialog) ;

        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MyAddressesIntent = new Intent(getContext(), MyAddressesActivity.class);
                MyAddressesIntent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(MyAddressesIntent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                CURR_USER = null;
                RegisterActivity.disableCloseButton = false;
                DBqueries.clearData();
                startActivity(new Intent(getContext() , RegisterActivity.class));
                getActivity().finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfoIntent = new Intent(getContext() , UpdateUserInfoActivity.class) ;
                updateUserInfoIntent.putExtra("Name" , name.getText().toString()) ;
                updateUserInfoIntent.putExtra("MobileNo" , mobileNo.getText().toString()) ;
                updateUserInfoIntent.putExtra("Photo" , DBqueries.profile) ;
                startActivity(updateUserInfoIntent) ;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(DBqueries.fullName);
        mobileNo.setText(DBqueries.mobileNo);
        if (!DBqueries.profile.equals("")) {
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(profileView) ;
        }else {
            profileView.setImageResource(R.drawable.ic_person_outline_black_24dp);
        }
        if (!loadingDialog.isShowing()) {
            if (DBqueries.addressesModelList.size() == 0) {
                addressName.setText("No Address");
                address.setText("_");
            }else {
                setAddress() ;
            }
        }
    }

    private void setAddress() {
        String nameText = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        String mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        name.setText(nameText);
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            addressName.setText(nameText + " - " + mobileNo);
        } else {
            addressName.setText(nameText + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        if (landmark.equals("")) {
            address.setText(flatNo + " , " + locality);
        } else {
            address.setText(flatNo + " , " + locality + " , " + landmark);
        }
    }
}
