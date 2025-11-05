package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> foodItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAddToCart(FoodItem foodItem);
    }

    public FoodItemAdapter(List<FoodItem> foodItems, OnItemClickListener listener) {
        this.foodItems = foodItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        holder.nameText.setText(item.getName());
        holder.descriptionText.setText(item.getDescription());
        holder.priceText.setText(String.format(Locale.getDefault(), "¥%.2f", item.getPrice()));
        holder.addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCart(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView descriptionText;
        TextView priceText;
        Button addButton;

        FoodViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.foodName);
            descriptionText = itemView.findViewById(R.id.foodDescription);
            priceText = itemView.findViewById(R.id.foodPrice);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}
