package com.example.bestquotes.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bestquotes.ImageSharingforUpdateAndroidVersions.SetupContext;
import com.example.bestquotes.LoginandRegistrationsForms.MainActivity;
import com.example.bestquotes.R;
import com.example.bestquotes.UserDashBoard;
import com.example.bestquotes.VeriablesClasses.BGImageModel;
import com.example.bestquotes.VeriablesClasses.FavouritesModel;
import com.example.bestquotes.VeriablesClasses.QuotesModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class QuotesAdapter extends FirestoreRecyclerAdapter<QuotesModel, QuotesAdapter.Holder> {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference reference = firestore.collection("Images");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    Context context;

    public QuotesAdapter(FirestoreRecyclerOptions<QuotesModel> options, Context act) {
        super(options);
        context = act;
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull QuotesModel model) {
        int rendom = new Random().nextInt(6 - 1) + 1; // generate rendom number less then 6 (from 1 to 5)
        SharedPreferences preferences = context.getSharedPreferences("Check_Status", Context.MODE_PRIVATE);
        boolean check = preferences.getBoolean("status", false);
        Dialog dialog = new Dialog(context);

        holder.textView.setText("\" " + model.getQuote_content() + " \"");
        holder.liketextview.setText(String.valueOf(model.getQuote_like()));
        holder.authorname.setText(model.getQuote_auther());
        holder.logotext.setVisibility(View.INVISIBLE);

        //fetching Images and set at background
        if (check == true) {
            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        BGImageModel obj = documentSnapshot.toObject(BGImageModel.class);
                        int id = obj.getId();
                        if (id == rendom) {
                            Glide.with(context).load(obj.getBg()).apply(RequestOptions.bitmapTransform(new BlurTransformation(20, 2))).into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        holder.layout.setBackground(resource);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }else{
            if (rendom == 1){
                holder.layout.setBackgroundColor(Color.parseColor("#4CAF50")); // green
            }else if (rendom == 2){
                holder.layout.setBackgroundColor(Color.parseColor("#FFBB86FC")); //purple
            }else if (rendom == 3){
                holder.layout.setBackgroundColor(Color.parseColor("#CF7D05")); //brown
            }else if (rendom == 4){
                holder.layout.setBackgroundColor(Color.parseColor("#FF03DAC5")); //teal
            }else{
                holder.layout.setBackgroundColor(Color.parseColor("#02A899"));
            }
        }

        //save image to gallary
        holder.downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.logotext.setVisibility(View.VISIBLE);
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
                    holder.logotext.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    Log.e("Falt","Some thing went Wrong...");
                }
            }
        });

        //share image
        holder.sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                holder.logotext.setVisibility(View.VISIBLE);
                Bitmap bitmap = Bitmap.createBitmap(holder.layout.getWidth(), holder.layout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                holder.layout.draw(canvas);
                try {
                    File file = new File(SetupContext.context.getExternalCacheDir(), "IMG_"+System.currentTimeMillis() +".png");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    file.setReadable(true, false);
                    Uri photoURL = FileProvider.getUriForFile(SetupContext.context, SetupContext.context.getApplicationContext().getPackageName()+".provider", file);

                    Intent shareintent = new Intent(Intent.ACTION_SEND);
                    shareintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareintent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareintent.putExtra(Intent.EXTRA_STREAM, photoURL);
                    shareintent.setType("image/png");
                    context.startActivity(Intent.createChooser(shareintent, "Share Image..."));
                }catch (Exception e){
                    Log.e("Error Detail: ", String.valueOf(e));
                }
                holder.logotext.setVisibility(View.INVISIBLE);
            }
        });

        //favourite button
        CollectionReference collectionReference = firestore.collection("users");
        if (user != null){
            String qid = model.getQuote_id();
            String uid = user.getUid();
            collectionReference.document(uid).collection("Fav").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                List<String> list = new ArrayList<String>();
                                for (QueryDocumentSnapshot snapshot :task.getResult()){
                                    String ids = snapshot.getId().toString();
                                    list.add(ids);
                                }
                                for (int i = 0 ; i<list.size() ; i++){
                                    boolean isExist = list.contains(qid);
                                    if (isExist == true){
                                        holder.favbtn.setImageResource(R.drawable.likeicon);
                                        holder.favbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                firestore.collection("users").document(uid)
                                                        .collection("Fav").document(qid).delete();

                                                int likes = (int) model.getQuote_like();
                                                likes--;
                                                firestore.collection("quotes").document(qid).update("quote_like", likes);
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }else{
                                        holder.favbtn.setImageResource(R.drawable.unlikeicon);
                                        holder.favbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Map<String, Object> addData = new HashMap<>();
                                                addData.put("QuoteID",qid);
                                                addData.put("UserID", uid);
                                                collectionReference.document(uid).collection("Fav").document(qid).set(addData);
                                                int likes = (int) model.getQuote_like();
                                                likes++;
                                                firestore.collection("quotes").document(qid).update("quote_like", likes);
                                            }
                                        });

                                    }
                                }
                            }
                        }
                    });

        }else{
                holder.favbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.setContentView(R.layout.login_ui);
                        dialog.setCancelable(true);
                        dialog.show();
                        Button signin , guest;
                        ProgressBar bar;
                        bar = dialog.findViewById(R.id.cardprogress);
                        bar.setVisibility(View.INVISIBLE);
                        signin = dialog.findViewById(R.id.card_signin);
                        guest = dialog.findViewById(R.id.card_guest);
                        signin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(context, MainActivity.class);
                                context.startActivity(i);
                            }
                        });

                        guest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bar.setVisibility(View.VISIBLE);
                                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){

                                            String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            Map<String, Object> obj = new HashMap<>();
                                            obj.put("post_likes", 0);
                                            obj.put("user_email", "null");
                                            obj.put("user_follow", 0);
                                            obj.put("user_follower", 0);
                                            obj.put("user_id", current_user_id);
                                            obj.put("user_isActive", true);
                                            obj.put("user_name", "null");
                                            obj.put("user_posts", 0);
                                            obj.put("user_profile_img", "Profile_Image");
                                            firestore.collection("users").document(current_user_id).set(obj);
                                            Toast.makeText(context, "Sign in as Guest", Toast.LENGTH_SHORT).show();

                                            FavouritesModel mod = new FavouritesModel(model.getQuote_id(), current_user_id);
                                            firestore.collection("users").document(current_user_id).collection("Fav").document(model.getQuote_id()).set(mod);

                                            firestore.collection("forAdmin").document("users_detail")
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                DocumentSnapshot snapshot = task.getResult();
                                                                int userstate = snapshot.getLong("total_isActive").intValue();
                                                                int totaluser = snapshot.getLong("total_users").intValue();
                                                                totaluser++;
                                                                userstate++;
                                                                Map<String, Object> obj = new HashMap<>();
                                                                obj.put("total_isActive", userstate);
                                                                obj.put("total_users", totaluser);
                                                                firestore.collection("forAdmin").document("users_detail").update(obj);
                                                            }
                                                        }
                                                    });


                                            UserDashBoard.activity.recreate();
                                            bar.setVisibility(View.GONE);
                                            dialog.dismiss();
                                        }
                                    }
                                });

                            }
                        });
                    }
                });
        }

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bgimagecard,parent,false);
        return new Holder(view);
    }


    public class Holder extends RecyclerView.ViewHolder{
        ImageView favbtn, downloadbtn, sharebtn;
        TextView textView, liketextview, logotext, authorname;
        RelativeLayout layout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            favbtn = itemView.findViewById(R.id.favouritebtn);
            textView = itemView.findViewById(R.id.quotes_content);
            liketextview = itemView.findViewById(R.id.total_likes);
            layout = itemView.findViewById(R.id.layout);
            downloadbtn = itemView.findViewById(R.id.download_btn);
            sharebtn = itemView.findViewById(R.id.share_btn);
            logotext = itemView.findViewById(R.id.signaturetext);
            authorname = itemView.findViewById(R.id.authorname);
        }
    }
}
