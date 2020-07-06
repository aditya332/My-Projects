package com.somanibrothersservices.lokalmall;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {
    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastposition=-1;
    private boolean fromHome;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
        this.fromHome = false;
    }

    public HomePageAdapter(List<HomePageModel> homePageModelList, boolean fromHome) {
        this.homePageModelList = homePageModelList;
        this.fromHome = fromHome;
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_LAYOUT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.STRIP_AD_BANNER:
                View stripAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdBannerViewHolder(stripAdView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_LAYOUT:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }

        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_BANNER:
                String resoure = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAdBannerViewHolder) holder).setStripAd(resoure, color);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                String title = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                ((HorizontalProductViewHolder) holder).setHorizontalLayout(horizontalProductScrollModelList, title , layoutColor , viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_LAYOUT:
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                String gridTitle = homePageModelList.get(position).getTitle();
                String gridLayoutColor =  homePageModelList.get(position).getBackgroundColor();
                ((GridProductViewHolder)holder).setGridProductLayout(gridProductScrollModelList , gridTitle , gridLayoutColor) ;
                break;
            default:
                return;
        }
        if(lastposition < position){
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private int currPage ;
        private Timer timer;
        final private long DELAY_TIME = 2000, PERIOD_TIME = 2000;
        private List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);

        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
            currPage = 1;
            if(timer != null){
                timer.cancel();
            }
            arrangedList = new ArrayList<>();

            for(int x=0;x<sliderModelList.size();x++){
                arrangedList.add(x,sliderModelList.get(x));
            }
            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            bannerSliderViewPager.setCurrentItem(currPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(sliderModelList);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideshow(sliderModelList);
            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(sliderModelList);
                    stopBannerSlideshow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideshow(sliderModelList);
                    }
                    return false;
                }
            });
        }

        ///Banner Slider
        private void pageLooper(List<SliderModel> sliderModelList) {
            if (currPage == sliderModelList.size() - 1) {
                currPage = 1;
                bannerSliderViewPager.setCurrentItem(currPage, false);
            }
            /*if (currPage == 1) {
                currPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currPage, false);
            }*/
        }

        private void startBannerSlideshow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currPage >= sliderModelList.size()) {
                        currPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currPage++, true);
                }
            };

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideshow() {
            timer.cancel();
        }
        ///Banner Slider
    }

    public class StripAdBannerViewHolder extends RecyclerView.ViewHolder {

        private ImageView stripAdImage;
        private ConstraintLayout stripAdContainer;

        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAdContainer = itemView.findViewById(R.id.strip_ad_container);
            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
        }

        private void setStripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(stripAdImage);
            stripAdContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container ;
        private TextView horizontalLayoutTitle;
        private Button horizontalLayoutviewAllButton;
        private RecyclerView horizontalLayoutRecyclerView;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.h_s_container);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalLayoutviewAllButton = itemView.findViewById(R.id.horizontal_scroll_vew_all_button);
            horizontalLayoutRecyclerView = itemView.findViewById(R.id.horizontal_scroll_layout_recyclerview);
            horizontalLayoutRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title , String color, final List<WishlistModel> viewAllProductList) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color))) ;
            horizontalLayoutTitle.setText(title);

            if (horizontalProductScrollModelList.size() > 8) {
                horizontalLayoutviewAllButton.setVisibility(View.VISIBLE);
                horizontalLayoutviewAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(),ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("title",title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            } else {
                horizontalLayoutviewAllButton.setVisibility(View.INVISIBLE);
            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalLayoutRecyclerView.setLayoutManager(linearLayoutManager1);
            horizontalLayoutRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private TextView gridLayoutTitle;
        private Button gridLayoutViewAllButton;
        //private GridView gridLayoutGridView;
        private GridLayout gridProductLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.grid_layout_container);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            gridLayoutViewAllButton = itemView.findViewById(R.id.grid_product_layout_viewall_button);
            //gridLayoutGridView = itemView.findViewById(R.id.grid_product_layout_gridview);
            gridProductLayout =itemView.findViewById(R.id.grid_layout);
        }

        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title , String color) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridLayoutTitle.setText(title);
            //gridLayoutGridView.setAdapter(new GridProductLayoutAdapter(horizontalProductScrollModelList));
            for(int x = 0; x< (Math.min(horizontalProductScrollModelList.size(), 4)); x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_layout_product_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_layout_product_title);
                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_layout_product_desc);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_layout_product_price);
                //productImage.setImageResource(horizontalProductScrollModelList.get(x).getProductImage());
                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo_album_grey_24dp)).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                if (fromHome) {
                    productDescription.setVisibility(View.INVISIBLE);
                    productPrice.setVisibility(View.INVISIBLE);
                } else {
                    productDescription.setText(horizontalProductScrollModelList.get(x).getProductDesc());
                    productPrice.setText("₹ " + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");
                }
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));
                if (!title.equals("")) {
                    final int finalX = x;
                    if (fromHome) {
                        gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent categoryIntent = new Intent(itemView.getContext(), CategoryActivity.class);
                                categoryIntent.putExtra("CategoryName", horizontalProductScrollModelList.get(finalX).getProductID());
                                itemView.getContext().startActivity(categoryIntent);
                            }
                        });
                    } else {
                        gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                                productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(finalX).getProductID());
                                itemView.getContext().startActivity(productDetailsIntent);
                            }
                        });
                    }
                }
            }

            if(!title.equals("")) {
                gridLayoutViewAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        if (fromHome) {
                            viewAllIntent.putExtra("layout_code", 2);
                        } else {
                            viewAllIntent.putExtra("layout_code", 1);
                        }
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            }
        }
    }

}
