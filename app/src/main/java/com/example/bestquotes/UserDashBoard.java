package com.example.bestquotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.example.bestquotes.Adapters.QuotesAdapter;
import com.example.bestquotes.DrawerMenuItems.CategoryQuotes;
import com.example.bestquotes.DrawerMenuItems.MostPopular;
import com.example.bestquotes.DrawerMenuItems.UserProfile;
import com.example.bestquotes.DrawerMenuItems.UserSetting;
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

public class UserDashBoard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    FirebaseFirestore firestore;
    FirebaseUser user;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    QuotesAdapter adapter;
    DocumentSnapshot lastvisable;
    boolean isScroll;
    boolean islastitemreached;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);
        activity = this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

            if (user.isAnonymous()){
                menu.findItem(R.id.menu_user_profile).setVisible(true);
            }
            String currentuser = user.getUid();
            firestore.collection("users").document(currentuser)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        boolean checkstate = snapshot.getBoolean("user_isActive");
                        if (checkstate == false){
                            Dialog dialog = new Dialog(UserDashBoard.this);
                            dialog.setContentView(R.layout.alertuser);
                            dialog.setCancelable(false);
                            dialog.show();
                            Button okbtn;
                            okbtn = dialog.findViewById(R.id.alertokbtn);
                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SharedPreferences loginpref;
                                    loginpref = getSharedPreferences("Login_Session", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = loginpref.edit();
                                    editor.clear();
                                    editor.commit();
                                    finish();
                                    FirebaseAuth.getInstance().signOut();
                                }
                            });
                        }
                    }
                }
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        recreate();
                        break;
                    case R.id.menu_user_profile:
                            Intent i = new Intent(UserDashBoard.this, UserProfile.class);
                            startActivity(i);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;
                    case R.id.categorylist:
                        Intent cat = new Intent(UserDashBoard.this, CategoryQuotes.class);
                        startActivity(cat);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.popular_quotes:
                        Intent pq = new Intent(UserDashBoard.this, MostPopular.class);
                        startActivity(pq);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.accountsetting:
                        Intent setting = new Intent(UserDashBoard.this, UserSetting.class);
                        startActivity(setting);
                        drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
            }
        });

        recyclerView = findViewById(R.id.imagerecyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = firestore.collection("quotes").limit(10);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<QuotesModel> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()){
                        QuotesModel model = snapshot.toObject(QuotesModel.class);
                        list.add(model);
                    }
                    adapter = new QuotesAdapter(list,UserDashBoard.this);
                    recyclerView.setAdapter(adapter);

                    lastvisable = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                                isScroll = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int firstVisableitem = layoutManager.findFirstVisibleItemPosition();
                            int visableitemcount = layoutManager.getChildCount();
                            int totalitem = layoutManager.getItemCount();

                            if (isScroll && (firstVisableitem + visableitemcount == totalitem) && !islastitemreached){
                                isScroll = false;
                                Query nextquery = firestore.collection("quotes")
                                        .startAfter(lastvisable).limit(10);
                                nextquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            QuotesModel model = snapshot.toObject(QuotesModel.class);
                                            list.add(model);
                                        }
                                        adapter.notifyDataSetChanged();
                                        lastvisable = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                        if (task.getResult().size() < 10){
                                            islastitemreached = true;
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){ }
            //showing message
        }
    }
}