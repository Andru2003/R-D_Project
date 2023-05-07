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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rdproject.Adapter.RandomRecipeAdapter;
import com.example.rdproject.Listeners.RandomRecipiResponseListener;
import com.example.rdproject.Models.RandomRApiResponse;

import java.util.ArrayList;
import java.util.List;


public class PopularFragment extends Fragment {

   // ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    // Here we create a spinner
    Spinner spinner;
    // We will create the list of tags
    List<String> tags = new ArrayList<>();

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
        // Here we will use the spinner to match the xmls with the string(dish types)
        spinner = view.findViewById(R.id.spinner1_tags);
        // here we create the Array Adapter
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.tags,
                R.layout.spinner_text
        );
        arrayAdapter.setDropDownViewResource(R.layout.spinner_inner_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(spinnerSelectedListener);

        manager = new RequestManager(view.getContext());
        //manager.getRandomRecipes(randomRecipeResponseListener);

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

    // Here we will add the listeners for every button in the dropdown
    private  final AdapterView.OnItemSelectedListener spinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tags.clear();
            tags.add(parent.getSelectedItem ().toString());
            manager.getRandomRecipes(randomRecipeResponseListener, tags);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

}