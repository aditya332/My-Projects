package com.somanibrothersservices.lokalmall;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GridProductLayoutAdapter extends BaseAdapter {
    List<HorizontalProductScrollModel> horizontalProductScrollModelList ;
    boolean fromHome ;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.fromHome = false;
    }

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList, boolean fromHome) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.fromHome = fromHome;
    }

    @Override
    public int getCount() {
        return horizontalProductScrollModelList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final View view ;
        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout , null) ;
            view.setElevation(0) ;
            view.setBackgroundColor(Color.parseColor("#ffffff")) ;

            if (fromHome) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryIntent = new Intent(view.getContext(), CategoryActivity.class);
                        categoryIntent.putExtra("CategoryName", horizontalProductScrollModelList.get(position).getProductTitle());
                        view.getContext().startActivity(categoryIntent);
                    }
                });
            } else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(parent.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(position).getProductID());
                        parent.getContext().startActivity(productDetailsIntent);
                    }
                });
            }

            ImageView productImage = view.findViewById(R.id.horizontal_scroll_item_layout_product_image) ;
            TextView productTitle = view.findViewById(R.id.horizontal_scroll_item_layout_product_title) ;
            TextView productDesc = view.findViewById(R.id.horizontal_scroll_item_layout_product_desc) ;
            TextView productPrice = view.findViewById(R.id.horizontal_scroll_item_layout_product_price) ;

            Glide.with(parent.getContext()).load(horizontalProductScrollModelList.get(position).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(productImage) ;
            productTitle.setText(horizontalProductScrollModelList.get(position).getProductTitle()) ;
            if (fromHome) {
                productDesc.setVisibility(View.INVISIBLE);
                productPrice.setVisibility(View.INVISIBLE);
            } else {
                productDesc.setText(horizontalProductScrollModelList.get(position).getProductDesc());
                productPrice.setText("₹ " + horizontalProductScrollModelList.get(position).getProductPrice());
            }
        }
        else {
            view = convertView ;
        }
        return view ;
    }

}
