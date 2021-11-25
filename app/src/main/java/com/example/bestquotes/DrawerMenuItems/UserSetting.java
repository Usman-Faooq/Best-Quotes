package com.example.bestquotes.DrawerMenuItems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.bestquotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserSetting extends AppCompatActivity {

    static CheckBox checkBox;
    Button logoutbtn, deleteaccountbtn;
    SharedPreferences preferences;
    boolean status;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        firestore = FirebaseFirestore.getInstance();

        checkBox = findViewById(R.id.enable_background_image);
        logoutbtn = findViewById(R.id.logoutbtn);
        deleteaccountbtn = findViewById(R.id.deleteaccountbtn);
        deleteaccountbtn.setVisibility(View.INVISIBLE);
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
        if(user != null){
            logoutbtn.setVisibility(View.VISIBLE);
            deleteaccountbtn.setVisibility(View.VISIBLE);
        }

        //delete account
        CollectionReference collectionReference = firestore.collection("forAdmin");
        deleteaccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builder = new AlertDialog.Builder(UserSetting.this)
                        .setTitle("Delete Account...")
                        .setMessage("are you sure you want to delete account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firestore.collection("users").document(user.getUid()).update("user_isActive",false);
                                collectionReference.document("users_detail")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot snapshot = task.getResult();
                                            int isActivestate = snapshot.getLong("total_isActive").intValue();
                                            int inActivestate = snapshot.getLong("total_inActive").intValue();
                                            isActivestate--;
                                            inActivestate++;
                                            Map<String, Object> obj = new HashMap<>();
                                            obj.put("total_inActive", inActivestate);
                                            obj.put("total_isActive",isActivestate);
                                            firestore.collection("forAdmin").document("users_detail").update(obj);

                                        }
                                    }
                                });

                                //Remove Screen For User After Deleting Account
                                SharedPreferences loginpref;
                                loginpref = getSharedPreferences("Login_Session",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = loginpref.edit();
                                editor.clear();
                                editor.commit();
                                finish();
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(UserSetting.this, "Delete Account Sucessfully", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(R.drawable.alerticon)
                        .show();
            }
        });

        //Logout Account
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