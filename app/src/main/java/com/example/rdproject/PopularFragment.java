package com.example.rdproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rdproject.Adapter.RandomRecipeAdapter;
import com.example.rdproject.Listeners.RandomRecipiResponseListener;
import com.example.rdproject.Models.RandomRApiResponse;


public class PopularFragment extends Fragment {

   // ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return   inflater.inflate(R.layout.fragment_popular, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      //  dialog = new ProgressDialog(view.getContext());
    //    dialog.setTitle("Loading");
        manager = new RequestManager(view.getContext());
        manager.getRandomRecipes(randomRecipeResponseListener);
        //dialog.show();

    }

    private final RandomRecipiResponseListener randomRecipeResponseListener = new RandomRecipiResponseListener() {
        @Override
        public void didFetch(RandomRApiResponse response, String message) {
        //    dialog.dismiss();
            recyclerView = getView().findViewById(R.id.recyclerRandom);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            randomRecipeAdapter = new RandomRecipeAdapter(getContext(), response.recipes);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

}