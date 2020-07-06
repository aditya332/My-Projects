package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    private RecyclerView myOrdersRecyclerView;
    public static MyOrderAdapter myOrderAdapter;
    private Dialog loadingDialog ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        loadingDialog = new Dialog(getContext()) ;
        loadingDialog.setContentView(R.layout.loading_progress_dialog) ;
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background)) ;
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT) ;
        loadingDialog.show();

        myOrdersRecyclerView = view.findViewById(R.id.my_orders_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myOrdersRecyclerView.setLayoutManager(linearLayoutManager);

        myOrderAdapter = new MyOrderAdapter(DBqueries.myOrderItemModelList , loadingDialog);
        myOrdersRecyclerView.setAdapter(myOrderAdapter);
        DBqueries.loadOrders(getContext(), myOrderAdapter , loadingDialog);

        //myOrderAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        myOrderAdapter.notifyDataSetChanged();
    }
}
