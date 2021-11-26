package com.example.bestquotes.DrawerMenuItems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bestquotes.Adapters.CategoryAdapter;
import com.example.bestquotes.R;
import com.example.bestquotes.VeriablesClasses.CategoryVeriables;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryQuotes extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseFirestore firestore;
    FirebaseUser user;
    RecyclerView recyclerView;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_quotes);


        Toolbar toolbar = findViewById(R.id.category_toolbar);
        toolbar.setTitle("Category");
        setSupportActionBar(toolbar);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.category_drawer_layout);
        navigationView = findViewById(R.id.category_navigation_view);
        Menu menu = navigationView.getMenu();
        if (user != null && user.isAnonymous()){
            menu.findItem(R.id.menu_user_profile).setVisible(true);
        }

        Dialog dialog = new Dialog(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        finish();
                        break;
                    case R.id.menu_user_profile:
                            Intent i = new Intent(CategoryQuotes.this, UserProfile.class);
                            startActivity(i);
                            drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.categorylist:
                        recreate();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.popular_quotes:
                        Intent mp = new Intent(CategoryQuotes.this, MostPopular.class);
                        startActivity(mp);
                        break;
                    case R.id.accountsetting:
                        Intent intent = new Intent(CategoryQuotes.this, UserSetting.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

        recyclerView = findViewById(R.id.category_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        Query query = firestore.collection("categories");
        FirestoreRecyclerOptions<CategoryVeriables> options = new
                FirestoreRecyclerOptions.Builder<CategoryVeriables>()
                .setQuery(query, CategoryVeriables.class).build();

        adapter = new CategoryAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}