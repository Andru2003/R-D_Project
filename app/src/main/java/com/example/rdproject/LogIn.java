package com.example.rdproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.android.material.textfield.TextInputLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button login_button;
    private TextView redirect_to_sign_up, forgot_psw;
    private ProgressBar progressBar;
    private TextInputEditText login_password, login_email;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        login_button = findViewById(R.id.LogIn_btn);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        redirect_to_sign_up = findViewById(R.id.text_to_signin);
        progressBar = findViewById(R.id.progress_bar);
        forgot_psw = findViewById(R.id.forgot);

        final ProgressDialog text = new ProgressDialog(this);
        text.setTitle("Loading");
        text.setMessage("Please wait while it`s loading ...");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_login_email = Objects.requireNonNull(login_email.getText()).toString();
                String user_login_password = Objects.requireNonNull(login_password.getText()).toString();


                //Query order = reference.orderByChild("email").equalTo(user_login_email);

                if(!user_login_email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(user_login_email).matches()){
                    if(!user_login_password.isEmpty()){
                        auth.signInWithEmailAndPassword(user_login_email, user_login_password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        text.show();
                                        Toast.makeText(LogIn.this, "LogIn Successful.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LogIn.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        text.hide();
                                        Toast.makeText(LogIn.this, "LogIn failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LogIn.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
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

        forgot_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                View dialog_view = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialog_view.findViewById(R.id.emailbox);

                builder.setView(dialog_view);
                AlertDialog dialog = builder.create();

                dialog_view.findViewById(R.id.resend_email).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = emailBox.getText().toString();

                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
                        {
                            Toast.makeText(LogIn.this, "Enter your registered e-mail", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(LogIn.this, "Verify your e-mail.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(LogIn.this, "Unable to send an e-mail.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialog_view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
    });

}
}