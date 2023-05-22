package com.example.rdproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdproject.Models.Step;
import com.example.rdproject.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder>{


    Context context;
    List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps, parent, false ));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {

        holder.textviewInstructionStepNumber.setText(String.valueOf(list.get(position).number));
        holder.textviewInstructionStepTitle.setText(list.get(position).step);

        holder.recyclerInstrctionsIngredients.setHasFixedSize(true);
        holder.recyclerInstrctionsIngredients.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsIngredientsAdapter instructionsIngredientsAdapter= new InstructionsIngredientsAdapter(context, list.get(position).ingredients);
        holder.recyclerInstrctionsIngredients.setAdapter(instructionsIngredientsAdapter);

        holder.recyclerInstrctionsEquipments.setHasFixedSize(true);
        holder.recyclerInstrctionsEquipments.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        InstructionsEquipmentsAdapter instructionsEquipmentsAdapter = new InstructionsEquipmentsAdapter(context, list.get(position).equipment);
        holder.recyclerInstrctionsEquipments.setAdapter(instructionsEquipmentsAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionStepViewHolder extends RecyclerView.ViewHolder {

    TextView textviewInstructionStepNumber,textviewInstructionStepTitle;
    RecyclerView recyclerInstrctionsEquipments, recyclerInstrctionsIngredients;

    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        textviewInstructionStepNumber = itemView.findViewById(R.id.textviewInstructionStepNumber);
        textviewInstructionStepTitle = itemView.findViewById(R.id.textviewInstructionStepTitle);
        recyclerInstrctionsEquipments = itemView.findViewById(R.id.recyclerInstrctionsEquipments);
        recyclerInstrctionsIngredients = itemView.findViewById(R.id.recyclerInstrctionsIngredients);
    }
}
