package com.example.rdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassword extends AppCompatActivity {

    EditText text_email;
    Button resend_email, cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        text_email = findViewById(R.id.email_forgot_password);
        resend_email = findViewById(R.id.button_resend_email);
        cancel = findViewById(R.id.cancel_button);


    }
}