package com.example.bestquotes.LoginandRegistrationsForms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestquotes.R;
import com.example.bestquotes.VeriablesClasses.FavouritesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewUserRegistration extends AppCompatActivity {

    TextInputEditText username, useremail, userpassword;
    TextView alreadyaccount;
    Button registration_btn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);

        username = findViewById(R.id.new_reg_username);
        useremail = findViewById(R.id.new_reg_useremail);
        userpassword = findViewById(R.id.new_reg_userpassword);
        registration_btn = findViewById(R.id.registration_btn);
        alreadyaccount = findViewById(R.id.already_account);
        progressBar = findViewById(R.id.registrationprogressbar);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        alreadyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = username.getText().toString().trim();
                String email = useremail.getText().toString().trim();
                String password = userpassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(NewUserRegistration.this, "All Fields Are Requried", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(NewUserRegistration.this, "Please Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                }else if (password.length() < 6){
                    Toast.makeText(NewUserRegistration.this, "At Least 6 Charater in Password", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    username.setEnabled(false);
                    useremail.setEnabled(false);
                    userpassword.setEnabled(false);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("post_likes", 0);
                                        user.put("user_email", email);
                                        user.put("user_follow", 0);
                                        user.put("user_follower", 0);
                                        user.put("user_id", current_user_id);
                                        user.put("user_isActive", true);
                                        user.put("user_name", name);
                                        user.put("user_posts", 0);
                                        user.put("user_profile_img", "Profile_Image");
                                        firestore.collection("users").document(current_user_id).set(user);

                                        //adding like document...
                                        FavouritesModel mod = new FavouritesModel("model.getQuote_id()", "current_user_id");
                                        firestore.collection("users").document(current_user_id).collection("Fav").document("null").set(mod);

                                        //Admin Status
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

                                        Toast.makeText(NewUserRegistration.this, "User Register Sucessfully...", Toast.LENGTH_SHORT).show();
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(NewUserRegistration.this, "Fail To Registration...", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        username.setEnabled(true);
                                        useremail.setEnabled(true);
                                        userpassword.setEnabled(true);
                                    }
                                }
                            });
                }
            }
        });
    }
}