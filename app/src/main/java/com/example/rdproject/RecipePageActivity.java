package com.example.rdproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.adapters.ProgressBarBindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rdproject.Adapter.IngredientsAdapter;
import com.example.rdproject.Adapter.InstructionsAdapter;
import com.example.rdproject.Adapter.SimilarRecipeAdapter;
import com.example.rdproject.Listeners.InstructionsListener;
import com.example.rdproject.Listeners.RecipeClickListener;
import com.example.rdproject.Listeners.RecipeDetailsListener;
import com.example.rdproject.Listeners.SimilarRecipesListener;
import com.example.rdproject.Models.InstructionsResponse;
import com.example.rdproject.Models.RecipeDetailsResponse;
import com.example.rdproject.Models.SimilarRecipeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipePageActivity extends AppCompatActivity {
    int id;
    TextView textView_meal_name;
    TextView textView_meal_source;
    ImageView imageView_meal_image;
    TextView textView_meal_summary;
    RecyclerView recycler_meal_ingredients;
    RecyclerView recyclerSimilarMeal, recyclerMealInstructions;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_recipe_page);
        findviews();
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener, id);
        manager.getSimilarRecipies(similarRecipesListener, id);
        manager.getInstructions(instructionsListener, id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details...");
        dialog.show();
    }

    private void findviews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textView_meal_source = findViewById(R.id.textView_meal_source);
        textView_meal_summary = findViewById(R.id.textView_meal_summary);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recyclerSimilarMeal = findViewById(R.id.recyclerSimilarMeal);
        recyclerMealInstructions = findViewById(R.id.recyclerMealInstructions);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
             dialog.dismiss();
             textView_meal_name.setText(response.title);
             textView_meal_source.setText(response.sourceName);
             textView_meal_summary.setText(response.summary);
            Picasso.get().load(response.image).into(imageView_meal_image);
            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipePageActivity.this, LinearLayoutManager.HORIZONTAL, false ));
            ingredientsAdapter = new IngredientsAdapter(RecipePageActivity.this, response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipePageActivity.this,message, Toast.LENGTH_SHORT).show();
        }
    };

    private final SimilarRecipesListener similarRecipesListener = new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {

            recyclerSimilarMeal.setHasFixedSize(true);
            recyclerSimilarMeal.setLayoutManager(new LinearLayoutManager(RecipePageActivity.this, LinearLayoutManager.HORIZONTAL, false));
            similarRecipeAdapter = new SimilarRecipeAdapter(RecipePageActivity.this, response,recipeClickListener );
            recyclerSimilarMeal.setAdapter(similarRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipePageActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecipePageActivity.this, RecipePageActivity.class)
                    .putExtra("id", id));
        }
    };


    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recyclerMealInstructions.setHasFixedSize(true);
            recyclerMealInstructions.setLayoutManager(new LinearLayoutManager(RecipePageActivity.this, LinearLayoutManager.VERTICAL, false));
            instructionsAdapter = new InstructionsAdapter(RecipePageActivity.this, response);
            recyclerMealInstructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {

        }
    };



}