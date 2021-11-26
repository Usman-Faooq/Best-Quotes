package com.example.bestquotes.DrawerMenuItems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bestquotes.Adapters.QuotesAdapter;
import com.example.bestquotes.R;
import com.example.bestquotes.VeriablesClasses.QuotesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MostPopular extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseFirestore firestore;
    FirebaseUser user;
    RecyclerView recyclerView;
    QuotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_popular);

        Toolbar toolbar = findViewById(R.id.popular_toolbar);
        toolbar.setTitle("Most Popular");
        setSupportActionBar(toolbar);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        drawerLayout = findViewById(R.id.popular_drawer_layout);
        navigationView = findViewById(R.id.popular_navigation_view);
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
                            Intent i = new Intent(MostPopular.this, UserProfile.class);
                            startActivity(i);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;
                    case R.id.categorylist:
                        Intent cat = new Intent(MostPopular.this, CategoryQuotes.class);
                        startActivity(cat);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.popular_quotes:
                        recreate();
                        break;
                    case R.id.accountsetting:
                        Intent intent = new Intent(MostPopular.this, UserSetting.class);
                        startActivity(intent);
                        drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

        recyclerView = findViewById(R.id.popular_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = firestore.collection("quotes").orderBy("quote_like", Query.Direction.DESCENDING).limit(25);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<QuotesModel> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()){
                        QuotesModel model = snapshot.toObject(QuotesModel.class);
                        list.add(model);
                    }
                    adapter = new QuotesAdapter(list, MostPopular.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
}