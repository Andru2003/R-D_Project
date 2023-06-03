package com.example.rdproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdproject.Listeners.RecipeClickListener;
import com.example.rdproject.Models.IngredientSearchResponse;
import com.example.rdproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import com.example.rdproject.Listeners.RecipeClickListener;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<IngredientSearchResponse> recipeList;
    private Context context;
    RecipeClickListener listener;


    public RecipeAdapter(List<IngredientSearchResponse> recipeList, Context context, RecipeClickListener listener) {
        this.recipeList = recipeList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_homescreen, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        IngredientSearchResponse recipe = recipeList.get(position);

        holder.titleTextView.setText(recipe.title);
        Picasso.get().load(recipe.image).into(holder.recipeImageView);

        holder.linearLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(recipeList.get(holder.getAbsoluteAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private ImageView recipeImageView;
        private TextView titleTextView;
        private CardView linearLayoutView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipe_image);
            titleTextView = itemView.findViewById(R.id.recipe_title);
            linearLayoutView = itemView.findViewById(R.id.recipe_recycler_view_mainpage);
        }
    }
}
