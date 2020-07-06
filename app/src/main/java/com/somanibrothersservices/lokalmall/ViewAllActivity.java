package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import java.util.List;

import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridView;
    public static List<WishlistModel> wishlistModelList;
    public static  List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        String title = getIntent().getStringExtra(getIntent().getStringExtra("title")) ;
        getSupportActionBar().setTitle("View All") ;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        gridView = findViewById(R.id.grid_view);
        int layout_code = getIntent().getIntExtra("layout_code",-1);
        if(layout_code == 0){

            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            WishlistAdapter adapter = new WishlistAdapter(wishlistModelList,false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if(layout_code==1) {
            gridView.setVisibility(View.VISIBLE);
            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList);
            gridView.setAdapter(gridProductLayoutAdapter);
        } else if(layout_code==2){
            gridView.setVisibility(View.VISIBLE);
            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList , true);
            gridView.setAdapter(gridProductLayoutAdapter);
        }
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
                Intent cartIntent = new Intent(ViewAllActivity.this, MainActivity.class);
                MainActivity.showCart = true;
                startActivity(cartIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
