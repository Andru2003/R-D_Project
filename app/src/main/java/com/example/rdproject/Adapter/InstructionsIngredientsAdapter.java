package com.example.rdproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdproject.Models.Ingredient;
import com.example.rdproject.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionsIngredientsAdapter extends RecyclerView.Adapter<InstructionIngredientsViewHolder> {


    Context context;
    List<Ingredient> list;

    public InstructionsIngredientsAdapter(Context context, List<Ingredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionIngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionIngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_step_items, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull InstructionIngredientsViewHolder holder, int position) {

        holder.textviewInstructionStepItem.setText(list.get(position).name);
        holder.textviewInstructionStepItem.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_250x250/" + list.get(position).image).into(holder.imageviewInstructionStepItems);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionIngredientsViewHolder extends RecyclerView.ViewHolder {


    ImageView imageviewInstructionStepItems;
    TextView textviewInstructionStepItem;

    public InstructionIngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageviewInstructionStepItems = itemView.findViewById(R.id.imageviewInstructionStepItems);
        textviewInstructionStepItem = itemView.findViewById(R.id.textviewInstructionStepItem);

    }
}
