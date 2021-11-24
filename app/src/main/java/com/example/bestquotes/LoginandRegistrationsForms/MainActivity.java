package com.example.bestquotes.LoginandRegistrationsForms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestquotes.R;
import com.example.bestquotes.UserDashBoard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextInputEditText login_email, login_password;
    Button login_button;
    TextView new_user_registration;
    CardView cardView;
    ProgressBar progressBar;
    FirebaseAuth auth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean logincheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardView = findViewById(R.id.cardview);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_btn);
        new_user_registration = findViewById(R.id.new_registration_btn);
        progressBar = findViewById(R.id.loginprogressbar);
        progressBar.setVisibility(View.INVISIBLE);

        preferences = getSharedPreferences("Login_Session", Context.MODE_PRIVATE);
        editor = preferences.edit();
        auth = FirebaseAuth.getInstance();

        boolean check = preferences.getBoolean("stay_login", false);
        if (check == true){
            Intent i = new Intent(MainActivity.this, UserDashBoard.class);
            startActivity(i);
        }

        new_user_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewUserRegistration.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(MainActivity.this, "All Fields Are Requried", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                }else if (password.length() < 6){
                    Toast.makeText(MainActivity.this, "At Least 6 Charater in Password", Toast.LENGTH_SHORT).show();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    login_email.setEnabled(false);
                    login_password.setEnabled(false);
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        logincheck = true;
                                        editor.putBoolean("stay_login", logincheck);
                                        editor.apply();
                                        Intent intent = new Intent(MainActivity.this, UserDashBoard.class);
                                        startActivity(intent);
                                        progressBar.setVisibility(View.GONE);
                                        login_email.setEnabled(true);
                                        login_password.setEnabled(true);
                                        login_email.setText("");
                                        login_password.setText("");
                                    }else{
                                        Toast.makeText(MainActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        login_email.setEnabled(true);
                                        login_password.setEnabled(true);
                                    }
                                }
                            });
                }
            }
        });


    }
}