package com.example.rdproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.example.rdproject.Listeners.RecipeClickListener;
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

    SearchView searchView;


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
      //  dialog.setTitle("Loading");


        // We initilise the search bar
        searchView = view.findViewById(R.id.searchBarView);
        //we set up a query text listener so that the recipies will change when we type the recipe we want
        //in the popular fragment the search will be by recipe name not by ingredients, the recipie by ingredient will be
        //in the home fragment
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //we will use just the method when the text is submiited on the query
            @Override
            public boolean onQueryTextSubmit(String query) {
                //all the current tags that we have are erased
                tags.clear();
                //we add our search tags
                tags.add(query);
                //the recipies are changed
                manager.getRandomRecipes(randomRecipeResponseListener, tags);
                //the method is enable
                return true;
            }

            //as our API has limited calls per day we will not use this method as
            //it would change our query continiously as we type our names and we do not want that
            //therefore, we will set it to false.
            @Override
            public boolean onQueryTextChange(String newText) {
                //the method is disabled.
                return false;
            }
        });

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
            randomRecipeAdapter = new RandomRecipeAdapter(getContext(), response.recipes, recipeClickListener);
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
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            Toast.makeText(getActivity(),id,Toast.LENGTH_SHORT);
        }
    };

}