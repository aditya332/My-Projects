package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_USER_DATA;
import static com.somanibrothersservices.lokalmall.SplashActivity.CURR_USER;
import static com.somanibrothersservices.lokalmall.SplashActivity.USER_FIREBASE_FIRESTORE;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static NotificationAdapter notificationAdapter ;
    private boolean runQuery = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        recyclerView = findViewById(R.id.recycler_view_notification) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) ;
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL) ;
        recyclerView.setLayoutManager(linearLayoutManager) ;

        notificationAdapter = new NotificationAdapter(DBqueries.notificationModelList) ;
        recyclerView.setAdapter(notificationAdapter);

        Map<String , Object> readMap = new HashMap<>() ;
        for (int x = 0 ;x < DBqueries.notificationModelList.size() ; x++) {
            if (!DBqueries.notificationModelList.get(x).isRead()) {
                runQuery = true ;
            }
            readMap.put("Read_" + x , true) ;
        }
        if (runQuery) {
            USER_FIREBASE_FIRESTORE.collection("USERS").document(CURR_USER.getUid()).collection(STORE_USER_DATA).document("MY_NOTIFICATIONS")
                    .update(readMap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int x = 0 ;x < DBqueries.notificationModelList.size() ; x++) {
            DBqueries.notificationModelList.get(x).setRead(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
