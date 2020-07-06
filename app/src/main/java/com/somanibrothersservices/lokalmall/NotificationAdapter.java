package com.somanibrothersservices.lokalmall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    private List<NotificationModel> notificationModelList ;

    public NotificationAdapter(List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public NotificationAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item , parent , false) ;
        return new viewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.viewHolder holder, int position) {
        String image = notificationModelList.get(position).getImage() ;
        String body = notificationModelList.get(position).getBody() ;
        boolean read = notificationModelList.get(position).isRead() ;
        holder.setData(image , body , read );
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size() ;
    }

    class viewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView ;
        private TextView textView ;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_notification_item) ;
            textView = itemView.findViewById(R.id.textView_notification_item) ;
        }

        private void setData(String image , String body , boolean read) {
            Glide.with(itemView.getContext()).load(image).into(imageView) ;
            if (read) {
                textView.setAlpha(0.5f);
            }else {
                textView.setAlpha(1f);
            }
            textView.setText(body) ;
        }
    }

}
