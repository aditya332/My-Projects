package com.somanibrothersservices.lokalmall;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private boolean fromSearch ;
    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private int lastposition = -1 ;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist=wishlist;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wishlist_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String productID = wishlistModelList.get(position).getProductID() ;
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        String rating = wishlistModelList.get(position).getRating();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String prevPrice = wishlistModelList.get(position).getPrevPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock() ;
        viewHolder.setData(productID , resource,title,rating,productPrice,prevPrice,paymentMethod , position , inStock) ;

        if (lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView rating;
        private View priceCut;
        private TextView productPrice;
        private TextView prevPrice;
        private TextView paymentMethod;
        private ImageButton deleteButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            rating = itemView.findViewById(R.id.tv_product_rating_miniview);
            priceCut = itemView.findViewById(R.id.price_cut);
            productPrice = itemView.findViewById(R.id.product_price);
            prevPrice = itemView.findViewById(R.id.prev_price);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        private void setData(String productID , String resource, String title, String averageRate, String price, String prevPriceValue, boolean payMethod , final int position , boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(productImage);
            //productImage.setImageResource(resource) ;
            productTitle.setText(title);

            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock) {
                linearLayout.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                prevPrice.setVisibility(View.VISIBLE);
                rating.setText(averageRate);
                productPrice.setText("₹ " + price + "/-");
                prevPrice.setText("₹ " + prevPriceValue + "/-");
                if (payMethod) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of Stock") ;
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                prevPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }

            if(wishlist){
                deleteButton.setVisibility(View.VISIBLE);
            }
            else {
                deleteButton.setVisibility(View.GONE);
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteButton.setEnabled(false) ;
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true ;
                        DBqueries.removeFromWishlist(position, itemView.getContext());
                    }
                    //Toast.makeText(itemView.getContext(), "Delete", Toast.LENGTH_LONG).show();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSearch) {
                        ProductDetailsActivity.fromSearch = true ;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID" , wishlistModelList.get(position).getProductID()) ;
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }

}
