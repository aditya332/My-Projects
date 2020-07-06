package com.somanibrothersservices.lokalmall;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private List<MyOrderItemModel> myOrderItemModelList;
    private Dialog loadingDialog ;

    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList , Dialog loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog ;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_order_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder viewHolder, int position) {
        String resource = myOrderItemModelList.get(position).getProductImage();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
        Date date;
        switch (orderStatus) {
            case "Ordered":
                date = myOrderItemModelList.get(position).getOrderedDate();
                break;
            case "Packed":
                date = myOrderItemModelList.get(position).getPackedDate();
                break;
            case "Delivered":
                date = myOrderItemModelList.get(position).getDeliveredDate();
                break;
            case "Cancelled":
                date = myOrderItemModelList.get(position).getCancelledDate();
                break;
            default:
                date = myOrderItemModelList.get(position).getCancelledDate();
                break;
        }
        String productId = myOrderItemModelList.get(position).getProductId();
        viewHolder.setData(resource, title, orderStatus, date, productId, position);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private ImageView orderIndicator;
        private TextView productTitle;
        private TextView deliveryStatus;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            orderIndicator = itemView.findViewById(R.id.order_indicator);
            deliveryStatus = itemView.findViewById(R.id.order_delivered_date);
        }

        private void setData(String resource, String title, String orderStatus, Date date, final String productId, final int position) {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);
            if (orderStatus.equals("Cancelled")) {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.buttonRed)));
            } else {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getColor(R.color.success)));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
            deliveryStatus.setText(orderStatus + " " + String.valueOf(simpleDateFormat.format(date)));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailIntent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    orderDetailIntent.putExtra("position", position);
                    itemView.getContext().startActivity(orderDetailIntent);
                }
            });
        }
    }

}
