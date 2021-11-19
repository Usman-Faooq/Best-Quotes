package com.example.bestquotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSetting extends AppCompatActivity {

    static CheckBox checkBox;
    Button logoutbtn;
    SharedPreferences preferences;
    boolean status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        checkBox = findViewById(R.id.enable_background_image);
        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setVisibility(View.INVISIBLE);
        preferences = getSharedPreferences("Check_Status", Context.MODE_PRIVATE);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkBox.isChecked()){
                    status = true;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("status", status);
                    editor.commit();
                    Toast.makeText(UserSetting.this, "Status Checked", Toast.LENGTH_SHORT).show();
                }else{
                    status = false;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("status", status);
                    editor.commit();
                    Toast.makeText(UserSetting.this, "Removed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences loginpref;
                loginpref = getSharedPreferences("Login_Session",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginpref.edit();
                editor.clear();
                editor.commit();
                finish();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserSetting.this, "Logout sucessfull", Toast.LENGTH_SHORT).show();
            }
        });

        boolean check = preferences.getBoolean("status", false);
        if (check == true){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }
    }
}