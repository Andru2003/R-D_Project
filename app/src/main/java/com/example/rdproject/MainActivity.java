package com.example.rdproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomappbar.BottomAppBar;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    AnimatedBottomBar animatedMenu;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animatedMenu = findViewById(R.id.animatedBottomBar);

        if(savedInstanceState==null)
        {
            animatedMenu.selectTabById(R.id.home1, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
        }

        animatedMenu.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;

                if(newTab.getId() == R.id.home1)
                {
                    fragment = new HomeFragment();
                }
                else if(newTab.getId() == R.id.popular1)
                {
                    System.out.println("Popular");
                    fragment = new PopularFragment();
                }
                else if(newTab.getId() == R.id.account1)
                {
                    System.out.println("Account");
                    fragment = new AccountFragment();
                }

                if(fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }
                else{
                    System.out.println("error in casting fragment");
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }

        });
    }


}