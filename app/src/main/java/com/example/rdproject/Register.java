package com.example.rdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button signin_button;
    private EditText signin_email, signin_password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        signin_button = findViewById(R.id.signin_buttton);
        signin_email = findViewById(R.id.signin_email);
        signin_password = findViewById(R.id.signin_pswd);
        progressBar = findViewById(R.id.progress_bar);

        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_signin_email = signin_email.getText().toString();
                String user_signin_password = signin_password.getText().toString();

                if(user_signin_email.isEmpty()) {
                    signin_email.setError("E-mail cannot be empty!");
                }
                if(user_signin_password.isEmpty()){
                    signin_password.setError("Password cannot be empty!");
                }else {
                    auth.createUserWithEmailAndPassword(user_signin_email, user_signin_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Register.this, "SignIn successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, LogIn.class));
                            } else {
                                Toast.makeText(Register.this, "SignIn failed. Please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
   }
    }