package com.example.rdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText signin_email, signin_password, confirm_password, username;
    private ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button signin_button = findViewById(R.id.signin_buttton);

        signin_email = findViewById(R.id.signin_email);
        signin_password = findViewById(R.id.signin_pswd);
        username = findViewById(R.id.username);
        confirm_password = findViewById(R.id.confirmPassword);

        TextView go_to_login = findViewById(R.id.text_to_login);

        progressBar = findViewById(R.id.progress_bar);

        final ProgressDialog text = new ProgressDialog(this);
        text.setTitle("Loading");
        text.setMessage("Please wait while it`s loading ...");

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_signin_email = Objects.requireNonNull(signin_email.getText()).toString();
                String user_signin_password = Objects.requireNonNull(signin_password.getText()).toString();
                String user_cofirm_password = Objects.requireNonNull(confirm_password.getText()).toString();
                String editusername = Objects.requireNonNull(username.getText()).toString();

                auth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users");

                if(user_signin_email.isEmpty()) {
                    signin_email.setError("E-mail cannot be empty!");
                }
                else if(editusername.isEmpty()) {
                    username.setError("Username cannot be empty!");
                }
                else if(user_signin_password.isEmpty()) {
                    signin_password.setError("Password cannot be empty!");
                } else if (user_signin_password.length() < 6) {
                    signin_password.setError("Password cannot be less than 6 characters!");
                } else if(!user_cofirm_password.matches(user_signin_password)) {
                    confirm_password.setError("Passwords don`t match.");
                }else {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("email", user_signin_email);
                    hashMap.put("username", editusername);
                    hashMap.put("password", user_signin_password);
                    hashMap.put("description", "");
                    hashMap.put("image", "");

                    Query checkemail = reference.orderByChild("email").equalTo(user_signin_email);
                    Query checkusername = reference.orderByChild("username").equalTo(editusername);

                     checkemail.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if (snapshot.exists()) signin_email.setError("This email is already used.");
                             else {
                                 signin_email.setError(null);
                                 checkusername.addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         if (snapshot.exists()) {
                                             username.setError("This username is already used.");
                                         }
                                         else{
                                             username.setError(null);
                                             auth.createUserWithEmailAndPassword(user_signin_email, user_signin_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                                     if(task.isSuccessful()) {
                                                         text.show();

                                                         Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 if(task.isSuccessful())
                                                                 {
                                                                     Toast.makeText(Register.this, "User registered successfully. Please verify your e-mail.", Toast.LENGTH_SHORT).show();
                                                                     signin_email.setText("");
                                                                     signin_password.setText("");
                                                                     user = auth.getCurrentUser();
                                                                     reference.child(user.getUid()).setValue(hashMap);
                                                                 }
                                                                 else {
                                                                     Toast.makeText(Register.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                                 }
                                                             }
                                                         });

                                                         Toast.makeText(Register.this, "SignIn successful.", Toast.LENGTH_SHORT).show();
                                                         startActivity(new Intent(Register.this, LogIn.class));
                                                     } else {
                                                         text.hide();
                                                         Toast.makeText(Register.this, "SignIn failed. Please try again!", Toast.LENGTH_SHORT).show();
                                                         System.out.println(Objects.requireNonNull(task.getException()).getMessage());
                                                     }
                                                 }
                                             });
                                         }
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
                }
            }
        });

        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, LogIn.class));
            }
        });
   }
    }