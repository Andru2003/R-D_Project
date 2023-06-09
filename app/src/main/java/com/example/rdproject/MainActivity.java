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

    //animated bar we use to navigate
    AnimatedBottomBar animatedMenu;

    //fragment manager used to switch the fragments we use
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize animatedMenu with the corresponding view ID in the layout
        animatedMenu = findViewById(R.id.animatedBottomBar);

        if(savedInstanceState==null)
        {
            // Select the "home1" tab as default
            animatedMenu.selectTabById(R.id.home1, true);
            // Get the FragmentManager instance
            fragmentManager = getSupportFragmentManager();
            // Create a new instance of HomeFragment
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, homeFragment).commit();
        }

        //Switching the fragments based on the button clicked on the navbar
        animatedMenu.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;
                // Create a new instance of HomeFragment when the "home1" tab is selected
                if(newTab.getId() == R.id.home1)
                {
                    fragment = new HomeFragment();
                }
                // Create a new instance of PopularFragment when the "popular1" tab is selected
                else if(newTab.getId() == R.id.popular1)
                {
                    System.out.println("Popular");
                    fragment = new PopularFragment();
                }
                // Create a new instance of AccountFragment when the "account1" tab is selected
                else if(newTab.getId() == R.id.account1)
                {
                    System.out.println("Account");
                    fragment = new AccountFragment();
                }

                if(fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    // Replace the fragmentContainer with the selected fragment
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                }
                else{
                    System.out.println("error in casting fragment");
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {
                // This method is called when a tab is reselected, but it is not implemented as we do not need to use it in our case
            }

        });
    }


}