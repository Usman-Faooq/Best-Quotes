package com.example.bestquotes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bestquotes.DrawerMenuItems.CategoryDetail;
import com.example.bestquotes.R;
import com.example.bestquotes.VeriablesClasses.CategoryVeriables;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CategoryAdapter extends FirestoreRecyclerAdapter<CategoryVeriables, CategoryAdapter.holder> {

    Context mycontext;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<CategoryVeriables> options, Context context) {
        super(options);
        mycontext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull holder holder, int position, @NonNull CategoryVeriables model) {
        String catname = model.getCategory();
        holder.cat_text.setText(catname);
        Glide.with(holder.cat_image.getContext()).load(model.getCategory_img()).into(holder.cat_image);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mycontext, CategoryDetail.class);
                intent.putExtra("Cat_Name", catname);
                mycontext.startActivity(intent);

            }
        });

    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorycard ,parent ,false);
        return new CategoryAdapter.holder(view);
    }


    public class holder extends RecyclerView.ViewHolder{
        ImageView cat_image;
        TextView cat_text;
        CardView card;
        public holder(@NonNull View itemView) {
            super(itemView);
            cat_image = itemView.findViewById(R.id.cat_imageview);
            cat_text = itemView.findViewById(R.id.cat_textview);
            card = itemView.findViewById(R.id.category_card);
        }
    }
}
