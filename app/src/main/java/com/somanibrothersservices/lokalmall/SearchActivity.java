package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.somanibrothersservices.lokalmall.ChooseCityActivity.CITY_FIREBASE_FIRESTORE;
import static com.somanibrothersservices.lokalmall.ChooseStoreActivity.STORE_PRODUCTS;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView ;
    private TextView textView ;
    private RecyclerView recyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view) ;
        textView = findViewById(R.id.textView_search) ;
        recyclerView = findViewById(R.id.recycler_view_search) ;

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this) ;
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayout) ;

        final List<WishlistModel> list = new ArrayList<>() ;
        final List<String> ids = new ArrayList<>() ;
        final Adapter adapter = new Adapter(list , false) ;
        adapter.setFromSearch(true);
        recyclerView.setAdapter(adapter);

        init();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                list.clear();
                ids.clear();
                final String[] tags = query.toLowerCase().split(" ") ;
                for (final String tag : tags) {
                    tag.trim() ;
                    CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).whereArrayContains("tags" , tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    WishlistModel model = new WishlistModel(documentSnapshot.getId()
                                            , documentSnapshot.get("product_image_1").toString()
                                            , documentSnapshot.get("product_title").toString()
                                            , documentSnapshot.get("average_rating").toString()
                                            , documentSnapshot.get("product_price").toString()
                                            , documentSnapshot.get("prev_price").toString()
                                            , (boolean) documentSnapshot.get("COD")
                                            , true);
                                    model.setTags((ArrayList<String>)documentSnapshot.get("tags"));
                                    if (!ids.contains(model.getProductID())) {
                                        list.add(model) ;
                                        ids.add(model.getProductID()) ;
                                    }
                                }
                                if (tag.equals(tags[tags.length - 1])) {
                                    if (list.size() == 0) {
                                        textView.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }else {
                                        textView.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter.getFilter().filter(query) ;
                                    }
                                }
                            }else {
                                Toast.makeText(SearchActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) ;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void init() {
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        CITY_FIREBASE_FIRESTORE.collection(STORE_PRODUCTS).document("All Products Title").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> lists = (List<String>) task.getResult().get("title_list");
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < lists.size(); i++) {
                        arrayList.add(lists.get(i));
                    }
                    final ArrayAdapter arrayAdapter = new ArrayAdapter(SearchActivity.this , android.R.layout.simple_dropdown_item_1line , arrayList);
                    searchAutoComplete.setAdapter(arrayAdapter);
                    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                        @Override
                        public boolean onSuggestionSelect(int position) {
                            searchAutoComplete.setText(arrayAdapter.getItem(position).toString());
                            searchView.setQuery(searchAutoComplete.getText().toString(), true);
                            return true;
                        }

                        @Override
                        public boolean onSuggestionClick(int position) {
                            searchAutoComplete.setText(arrayAdapter.getItem(position).toString());
                            searchView.setQuery(searchAutoComplete.getText().toString(), true);
                            return true;
                        }
                    });

                } else {
                    Toast.makeText(SearchActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class Adapter extends WishlistAdapter implements Filterable {

        private List<WishlistModel> originalList ;
        public Adapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
            super(wishlistModelList, wishlist);
            originalList = wishlistModelList ;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults() ;
                    List<WishlistModel> filteredList = new ArrayList<>() ;

                    final String[] tags = constraint.toString().toLowerCase().split(" ") ;
                    for (WishlistModel model : originalList) {
                        ArrayList<String> presentTags = new ArrayList<>() ;
                        for (String tag : tags) {
                            if (model.getTags().contains(tag)) {
                                presentTags.add(tag) ;
                            }
                        }
                        model.setTags(presentTags) ;
                    }
                    for (int i = tags.length ; i > 0 ; i--) {
                        for (WishlistModel model : originalList) {
                            if (model.getTags().size() == i) {
                                filteredList.add(model) ;
                            }
                        }
                    }
                    filterResults.values = filteredList ;
                    filterResults.count = filteredList.size() ;
                    return filterResults ;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.count > 0) {
                        setWishlistModelList((List<WishlistModel>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}
