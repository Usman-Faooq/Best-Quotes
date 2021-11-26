package com.example.bestquotes.DrawerMenuItems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

public class CategoryDetail extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView imageView;
    FirebaseFirestore firestore;
    FirebaseUser user;
    RecyclerView recyclerView;
    QuotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.cattoolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.categorydetail_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.nodatafound);
        String condition = getIntent().getStringExtra("Cat_Name");
        toolbar.setTitle("Category: \""+condition+"\"");

        Query query = firestore.collection("quotes").whereEqualTo("quote_category" ,condition);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().isEmpty()){
                    recyclerView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                }
                if (task.isSuccessful()){
                    List<QuotesModel> list = new ArrayList<>();
                    for (DocumentSnapshot snapshot : task.getResult()){
                        QuotesModel model = snapshot.toObject(QuotesModel.class);
                        list.add(model);
                    }
                    adapter = new QuotesAdapter(list, CategoryDetail.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

}