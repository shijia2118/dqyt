package com.hxjt.dqyt.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.ViewHolder> {
    private String[] items;
    private boolean[] checkedItems;

    public MultiSelectAdapter(String[] items, boolean[] checkedItems) {
        this.items = items;
        this.checkedItems = checkedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkedTextView.setText(items[position]);
        holder.checkedTextView.setChecked(checkedItems[position]);
        holder.itemView.setOnClickListener(v -> {
            boolean isChecked = !checkedItems[position];
            checkedItems[position] = isChecked;
            holder.checkedTextView.setChecked(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView checkedTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}

