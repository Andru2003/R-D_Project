package com.example.rdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.rdproject.R.id;

public class MainActivity extends AppCompatActivity {

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Daca vedeti asta inseamna ca e bine");
        System.out.println("Sanatate si numai bine!");
        System.out.println("ce faci?");
        System.out.println("Ascultam ploaia!");
        System.out.println("dar nu ploaua");
        System.out.println("Nu ploua? Nu cred");
        System.out.println("Nu bagati de seama culoarea la meniu trebuie facut un collor pallete pt aplicatie");
        System.out.println("Stefan si a pierdut cheile de la casa io mor.");

        btn = findViewById(R.id.main_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });

    }
}