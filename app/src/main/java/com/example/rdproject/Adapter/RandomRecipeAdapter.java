package com.example.rdproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdproject.Listeners.RecipeClickListener;
import com.example.rdproject.Models.Recipe;
import com.example.rdproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RandomRecipeAdapter extends RecyclerView.Adapter<RandomRecipeViewHolder> {

    Context context;
    // Here we create a listener regarding opening certain recipe pages
    RecipeClickListener listener;
    List<Recipe> list;

    public RandomRecipeAdapter(Context context, List<Recipe> list, RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RandomRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RandomRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RandomRecipeViewHolder holder, int position) {
        holder.randomRecipeTitle.setText(list.get(position).title);
        holder.randomRecipeTitle.setSelected(true); //pt rotire
        holder.randomRecipeLike.setText(list.get(position).aggregateLikes + " Likes");
        holder.randomRecipeServings.setText(list.get(position).servings + " Servings");
        holder.randomRecipeTime.setText(list.get(position).readyInMinutes + " Minutes");
        //sets the images for the food
        Picasso.get().load(list.get(position).image).into(holder.randomRecipeImage);
        holder.randomContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAbsoluteAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class  RandomRecipeViewHolder extends RecyclerView.ViewHolder{
    CardView randomContainer;
    TextView randomRecipeTitle, randomRecipeServings, randomRecipeTime, randomRecipeLike;
    ImageView randomRecipeImage;

    public RandomRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        randomContainer = itemView.findViewById(R.id.randomContainer);
        randomRecipeTitle = itemView.findViewById(R.id.randomRecipeTitle);
        randomRecipeServings = itemView.findViewById(R.id.randomRecipeServings);
        randomRecipeTime = itemView.findViewById(R.id.randomRecipeTime);
        randomRecipeLike = itemView.findViewById(R.id.randomRecipeLike);
        randomRecipeImage = itemView.findViewById(R.id.randomRecipeImage);

    }
}
