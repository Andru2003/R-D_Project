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
import com.example.rdproject.Models.SimilarRecipeResponse;
import com.example.rdproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarRecipeAdapter extends RecyclerView.Adapter<SimilarRecipeViewHolder>{

    Context context;
    List<SimilarRecipeResponse> list;
    RecipeClickListener l;

    public SimilarRecipeAdapter(Context context, List<SimilarRecipeResponse> list, RecipeClickListener l) {
        this.context = context;
        this.list = list;
        this.l = l;
    }



    @NonNull
    @Override
    public SimilarRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimilarRecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.list_similar_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarRecipeViewHolder holder, int position) {
        holder.similarTitle.setText(list.get(position).title);
        holder.similarTitle.setSelected(true);
        holder.similarServing.setText(list.get(position).servings + "Persons");
        Picasso.get().load("https://spoonacular.com/recipeImages/" + list.get(position).id +"-556x370." + list.get(position).imageType).into(holder.imageViewSimilar);

        holder.similarRecipeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onRecipeClicked(String.valueOf(list.get(holder.getAbsoluteAdapterPosition()).id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class SimilarRecipeViewHolder extends RecyclerView.ViewHolder{

    CardView similarRecipeHolder;
    TextView similarTitle;
    ImageView imageViewSimilar;
    TextView similarServing;




    public SimilarRecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        similarRecipeHolder=itemView.findViewById(R.id.similarRecipeHolder);
        similarTitle=itemView.findViewById(R.id.similarTitle);
        imageViewSimilar=itemView.findViewById(R.id.imageViewSimilar);
        similarServing=itemView.findViewById(R.id.similarServing);
    }
}
