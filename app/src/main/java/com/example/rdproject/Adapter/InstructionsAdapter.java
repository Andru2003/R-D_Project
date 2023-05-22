package com.example.rdproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdproject.Models.InstructionsResponse;
import com.example.rdproject.R;

import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsViewHolder> {


    Context context;
    List<InstructionsResponse> list;

    public InstructionsAdapter(Context context, List<InstructionsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsViewHolder holder, int position) {
        holder.textviewInstruction.setText(String.valueOf(list.get(position).name));
        holder.recyclerSteps.setHasFixedSize(true);
        holder.recyclerSteps.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        InstructionStepAdapter stepAdapter = new InstructionStepAdapter(context, list.get(position).steps);
        holder.recyclerSteps.setAdapter(stepAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionsViewHolder extends RecyclerView.ViewHolder {

    TextView textviewInstruction;
    RecyclerView recyclerSteps;

    public InstructionsViewHolder(@NonNull View itemView) {
        super(itemView);

        textviewInstruction = itemView.findViewById(R.id.textviewInstruction);
        recyclerSteps = itemView.findViewById(R.id.recyclerSteps);

    }
}
