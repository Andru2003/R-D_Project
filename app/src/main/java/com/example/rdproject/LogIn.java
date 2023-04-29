package com.example.rdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button login_button;
    private EditText login_email, login_password;
    private TextView redirect_to_sign_up;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        login_button = findViewById(R.id.LogIn_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_pswd);
        redirect_to_sign_up = findViewById(R.id.text_to_signin);
        progressBar = findViewById(R.id.progress_bar);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_login_email = login_email.getText().toString();
                String user_login_password = login_password.getText().toString();

                if(!user_login_email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(user_login_email).matches()){
                    if(!user_login_password.isEmpty()){
                        auth.signInWithEmailAndPassword(user_login_email, user_login_password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogIn.this, "LogIn Successful.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LogIn.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogIn.this, "LogIn failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        login_password.setError("Password cannot be empty!");
                    }
                }else if(user_login_email.isEmpty()){
                    login_email.setError("E-mail cannot be empty!");
                }else {
                    login_email.setError("Please enter a valid e-mail.");
                }
            }
        });

        redirect_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, Register.class));
            }
        });
    }
}