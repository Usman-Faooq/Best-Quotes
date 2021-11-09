package com.example.bestquotes.LoginandRegistrationsForms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bestquotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewUserRegistration extends AppCompatActivity {

    TextInputEditText username, useremail, userpassword;
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
        progressBar = findViewById(R.id.registrationprogressbar);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = username.getText().toString().trim();
                String email = useremail.getText().toString().trim();
                String password = userpassword.getText().toString().trim();
                DocumentReference reference = firestore.collection("users").document();
                String user_id = reference.getId();

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
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("post_likes", 0);
                                        user.put("user_email", email);
                                        user.put("user_follow", 0);
                                        user.put("user_follower", 0);
                                        user.put("user_id", user_id);
                                        user.put("user_isActive", true);
                                        user.put("user_name", name);
                                        user.put("user_posts", 0);
                                        user.put("user_profile_img", "profile_ pics");

                                        firestore.collection("users").document(user_id).set(user);
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