package com.example.bestquotes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bestquotes.LoginandRegistrationsForms.MainActivity;
import com.example.bestquotes.R;
import com.example.bestquotes.VeriablesClasses.BGImageModel;
import com.example.bestquotes.VeriablesClasses.QuotesModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class QuotesAdapter extends FirestoreRecyclerAdapter<QuotesModel, QuotesAdapter.Holder> {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference reference = firestore.collection("Images");
    Context context;
    public QuotesAdapter(@NonNull FirestoreRecyclerOptions<QuotesModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull QuotesModel model) {
        int rendom = new Random().nextInt(6 - 1) + 1; // generate rendom number less then 6 (from 1 to 5)

        //fetching Images
        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    BGImageModel obj = documentSnapshot.toObject(BGImageModel.class);
                    int id = obj.getId();
                    if (id == rendom){
                        Glide.with(context).load(obj.getBg()).apply(RequestOptions.bitmapTransform(new BlurTransformation(20, 2))).into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                                    holder.layout.setBackground(resource);
                                    holder.textView.setText("\" " + model.getQuote_content() + " \"");
                                }
                            }
                        });
                    }
                }
            }
        });

        //save image to gallary
        holder.downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = Bitmap.createBitmap(holder.layout.getWidth(), holder.layout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                holder.layout.draw(canvas);
                try {
                    File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File dir = new File(storage.getAbsolutePath() + "/Quotes_Images");
                    if (!dir.exists()){
                        dir.mkdirs();
                    }

                    String fileName = String.format("%d.jpg", System.currentTimeMillis());
                    File outFile = new File(dir, fileName);

                    FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.close();
                    Toast.makeText(context, "Save Sucessfully", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Log.e("Falt","Some thing went Wrong...");
                }
            }
        });

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bgimagecard,parent,false);
        return new Holder(view);
    }


    public class Holder extends RecyclerView.ViewHolder{
        ImageView favbtn, downloadbtn;
        TextView textView;
        RelativeLayout layout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            favbtn = itemView.findViewById(R.id.like_unlike);
            textView = itemView.findViewById(R.id.quotes_content);
            layout = itemView.findViewById(R.id.layout);
            downloadbtn = itemView.findViewById(R.id.download_btn);
        }
    }
}
