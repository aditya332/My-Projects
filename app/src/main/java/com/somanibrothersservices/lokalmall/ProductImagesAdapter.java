package com.somanibrothersservices.lokalmall;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ProductImagesAdapter extends PagerAdapter {

    private List<String> productImages ;
    LayoutInflater inflater;

    public ProductImagesAdapter(List<String> productImages) {
        this.productImages = productImages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) container.getContext().getSystemService(container.getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.product_image_viewpager,container,false);
        PhotoView photoView = view.findViewById(R.id.product_image_photoview);
        Glide.with(container.getContext()).load(productImages.get(position)).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(photoView);
        container.addView(view) ;
        return view ;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object) ;
    }

    @Override
    public int getCount() {
        return productImages.size() ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object ;
    }
}
