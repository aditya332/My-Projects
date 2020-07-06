package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerview ;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        String title = getIntent().getStringExtra("CategoryName") ;
        setTitle(title) ;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;

        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null", "#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null", "#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null", "#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null", "#FFFFFF"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homePageModelFakeList.add(new HomePageModel(0, sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1, "","#FFFFFF"));
        homePageModelFakeList.add(new HomePageModel(2, "","#FFFFFF",horizontalProductScrollModelFakeList,new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3, "","#FFFFFF",horizontalProductScrollModelFakeList));


        categoryRecyclerview = findViewById(R.id.category_recyclerview) ;

        ///Banner Slider
        //List<SliderModel> sliderModelList = new ArrayList<SliderModel>() ;
        ///Banner Slider

        ///Horizontal Product Layout
        List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<HorizontalProductScrollModel>() ;        ///Horizontal Product Layout

        /////////////////
        LinearLayoutManager testingLayoutManger = new LinearLayoutManager(this) ;
        testingLayoutManger.setOrientation(RecyclerView.VERTICAL) ;
        categoryRecyclerview.setLayoutManager(testingLayoutManger) ;
        adapter = new HomePageAdapter(homePageModelFakeList) ;

        int listPosition = 0 ;
        for(int x = 0 ; x < DBqueries.loadCategoriesNames.size() ; x++){
            if(DBqueries.loadCategoriesNames.get(x).equals(title.toUpperCase())){
                listPosition = x ;
            }
        }
        if(listPosition == 0){
            DBqueries.loadCategoriesNames.add(title.toUpperCase());
            DBqueries.lists.add(new ArrayList<HomePageModel>());
            //adapter = new HomePageAdapter(lists.get(loadCategoriesNames.size() - 1)) ;
            DBqueries.loadFragmentData(categoryRecyclerview, this,DBqueries.loadCategoriesNames.size()-1,title);
        }else{
            adapter = new HomePageAdapter(DBqueries.lists.get(listPosition));
        }

        categoryRecyclerview.setAdapter(adapter) ;
        adapter.notifyDataSetChanged() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CURR_USER == null) {
            getMenuInflater().inflate(R.menu.search_icon, menu);
        } else {
            getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        }
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId() ;

        if (id == R.id.main_search_icon) {
            Intent searchIntent = new Intent(this , SearchActivity.class) ;
            startActivity(searchIntent) ;
            return true ;
        }else if (id == android.R.id.home) {
            finish() ;
            return true ;
        } else if (id == R.id.main_cart_icon) {
                Intent cartIntent = new Intent(CategoryActivity.this, MainActivity.class);
                MainActivity.showCart = true;
                startActivity(cartIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
