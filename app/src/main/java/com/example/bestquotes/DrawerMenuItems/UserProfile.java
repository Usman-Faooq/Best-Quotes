package com.example.bestquotes.DrawerMenuItems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bestquotes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    TextInputEditText name, email, password;
    TextView helptext;
    Button update, updatedone;
    ProgressBar bar;

    String username, useremail;

    FirebaseUser user;
    DocumentReference reference;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        bar = findViewById(R.id.updateprogressBar);
        name = findViewById(R.id.username);
        email = findViewById(R.id.useremail);
        password = findViewById(R.id.userpassword);
        helptext = findViewById(R.id.helptext);
        update = findViewById(R.id.updatebtn);
        updatedone = findViewById(R.id.updatedonebtn);

        updatedone.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
        name.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);


        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user !=null){

            String userid = user.getUid();
            reference = firestore.collection("users").document(userid);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null){
                            username = snapshot.getString("user_name");
                            useremail = snapshot.getString("user_email");

                            name.setText(username);
                            email.setText(useremail);
                        }
                    }
                }
            });

            //assigning email to anonyoumos user
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    update.setVisibility(View.INVISIBLE);
                    updatedone.setVisibility(View.VISIBLE);
                    if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
                        Toast.makeText(UserProfile.this, "Guest User", Toast.LENGTH_SHORT).show();
                        name.setEnabled(true);
                        email.setEnabled(true);
                        password.setEnabled(true);
                        name.setText("");
                        email.setText("");
                    }
                }
            });

            updatedone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){

                        String uname = name.getText().toString();
                        String uemail = email.getText().toString();
                        String upass = password.getText().toString();

                        if (uname.isEmpty() || uemail.isEmpty() || upass.isEmpty()){
                            Toast.makeText(UserProfile.this, "All Fields Requried", Toast.LENGTH_SHORT).show();
                        }else if (!Patterns.EMAIL_ADDRESS.matcher(uemail).matches()){
                            Toast.makeText(UserProfile.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                        }else if (upass.length() <6){
                            Toast.makeText(UserProfile.this, "At Least 6 Charater In Password", Toast.LENGTH_SHORT).show();
                        }else{
                            bar.setVisibility(View.VISIBLE);
                            AuthCredential credential = EmailAuthProvider.getCredential(uemail,upass);
                            user.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(UserProfile.this, "Link Sucessfully...", Toast.LENGTH_SHORT).show();
                                        String currentuser = user.getUid().toString();
                                        Map<String, Object> obj = new HashMap<>();
                                        obj.put("user_name", uname);
                                        obj.put("user_email", uemail);
                                        firestore.collection("users").document(currentuser).update(obj);
                                        bar.setVisibility(View.INVISIBLE);
                                    }else{
                                        Toast.makeText(UserProfile.this, "Unable to Link...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            recreate();
                        }
                    }
                }
            });
        }
    }
}