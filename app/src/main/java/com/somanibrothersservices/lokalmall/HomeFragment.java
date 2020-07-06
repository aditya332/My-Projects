package com.somanibrothersservices.lokalmall;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_WELCOME_STATEMENT;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private NetworkInfo networkInfo ;
    private ConnectivityManager connectivityManager ;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private RecyclerView homepageRecyclerview;
    private HomePageAdapter adapter ;
    private ImageView noInternetConnection ;
    private Button retryButton , aboutStore;
    private TextView welcome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle(STORE);
        noInternetConnection = view.findViewById(R.id.no_internet_connection) ;
        retryButton = view.findViewById(R.id.retry_button) ;
        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
        homepageRecyclerview = view.findViewById(R.id.home_page_recyclerview);
        connectivityManager = (ConnectivityManager)getActivity().getSystemService(CONNECTIVITY_SERVICE) ;
        networkInfo = connectivityManager.getActiveNetworkInfo() ;
        aboutStore = view.findViewById(R.id.fab_about_store);

        if (networkInfo != null && networkInfo.isConnected() == true) {
            TextView textView = view.findViewById(R.id.text_view_store_welcome);
            textView.setText(STORE_WELCOME_STATEMENT.replace("\\n" , "\n"));
            aboutStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AboutUsDialog aboutUsDialog = new AboutUsDialog(getContext() , true , false);
                }
            });
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) ;
            categoryRecyclerView.setVisibility(View.VISIBLE) ;
            homepageRecyclerview.setVisibility(View.VISIBLE) ;
            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);


            swipeRefreshLayout = view.findViewById(R.id.refresh_layout) ;
            swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary), getContext().getResources().getColor(R.color.colorPrimary), getContext().getResources().getColor(R.color.colorPrimary));
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            categoryRecyclerView.setLayoutManager(linearLayoutManager);

            LinearLayoutManager testingLayoutManger = new LinearLayoutManager(getContext());
            testingLayoutManger.setOrientation(RecyclerView.VERTICAL);
            homepageRecyclerview.setLayoutManager(testingLayoutManger);

            categoryModelFakeList.add(new CategoryModel("null", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));
            categoryModelFakeList.add(new CategoryModel("", ""));


            //////////////////////////////Categories Fake List

            //////////////////////////////HomePage Fake List

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
            homePageModelFakeList.add(new HomePageModel(1, "", "#FFFFFF"));
            homePageModelFakeList.add(new HomePageModel(2, "", "#FFFFFF", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));
            homePageModelFakeList.add(new HomePageModel(3, "", "#FFFFFF", horizontalProductScrollModelFakeList));

            //////////////////////////////HomePage Fake List
            categoryAdapter = new CategoryAdapter(categoryModelFakeList);


            adapter = new HomePageAdapter(homePageModelFakeList);


            if (DBqueries.categoryModelList.size() == 0) {
                DBqueries.loadCategories(categoryRecyclerView , getContext()) ;
            } else {
                categoryAdapter = new CategoryAdapter(DBqueries.categoryModelList) ;
                categoryAdapter.notifyDataSetChanged() ;
            }
            categoryRecyclerView.setAdapter(categoryAdapter);

            if (DBqueries.lists.size() == 0) {
                DBqueries.loadCategoriesNames.add("HOME") ;
                DBqueries.lists.add(new ArrayList<HomePageModel>()) ;
                DBqueries.loadFragmentData(homepageRecyclerview , getContext() , 0 , "HOME") ;
            } else {
                adapter = new HomePageAdapter(DBqueries.lists.get(0)) ;
                adapter.notifyDataSetChanged() ;
            }
            homepageRecyclerview.setAdapter(adapter);
            welcome = view.findViewById(R.id.text_view_welcome);
            if (DBqueries.fullName == null) {
                welcome.setText("Namaste \uEAD2 , ji ");
            } else {
                welcome.setText("Namaste \uEAD2 , " + DBqueries.fullName + " ji ");
            }
        }else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE) ;
            homepageRecyclerview.setVisibility(View.GONE) ;
            //noInternetConnection.setImageResource(R.drawable.ic_home_red_24dp) ;
            Glide.with(this).load(R.drawable.no_internet_connection).into(noInternetConnection) ;
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                //reloadPage() ;
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage() ;
            }
        });

        /////////////////

        return view;
    }

    private void reloadPage() {
        //categoryModelList.clear();
        //lists.clear();
        //loadCategoriesNames.clear();
        DBqueries.clearData();
        networkInfo = connectivityManager.getActiveNetworkInfo() ;
        if (networkInfo != null && networkInfo.isConnected()) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) ;
            categoryRecyclerView.setVisibility(View.VISIBLE) ;
            homepageRecyclerview.setVisibility(View.VISIBLE) ;
            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            DBqueries.loadCategories(categoryRecyclerView, getContext());
            DBqueries.loadCategoriesNames.add("HOME");
            DBqueries.lists.add(new ArrayList<HomePageModel>());
            DBqueries.loadFragmentData(homepageRecyclerview, getContext(), 0, "Home");
            homepageRecyclerview.setAdapter(adapter);

        } else {
            Toast.makeText(getContext(), "No Internet Connection !!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE) ;
            homepageRecyclerview.setVisibility(View.GONE) ;
            Glide.with(getContext()).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

            swipeRefreshLayout.setRefreshing(false) ;
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED) ;
        }
    }
}
