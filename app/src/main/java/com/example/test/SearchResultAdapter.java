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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<FoodItem> foodItems;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem);
    }

    public SearchResultAdapter(List<FoodItem> foodItems, OnItemClickListener listener) {
        this.foodItems = foodItems;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);

        // 设置餐厅名称
        holder.restaurantNameText.setText(foodItem.getRestaurantName());

        // 设置菜品信息
        holder.foodNameText.setText(foodItem.getName());
        holder.categoryText.setText(foodItem.getCategory());
        holder.descriptionText.setText(foodItem.getDescription());
        holder.priceText.setText(String.format(Locale.US, "¥%.2f", foodItem.getPrice()));

        // 添加按钮点击事件
        holder.addButton.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(foodItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItems != null ? foodItems.size() : 0;
    }

    public void updateData(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantNameText;
        TextView foodNameText;
        TextView categoryText;
        TextView descriptionText;
        TextView priceText;
        Button addButton;

        ViewHolder(View itemView) {
            super(itemView);
            restaurantNameText = itemView.findViewById(R.id.restaurantNameText);
            foodNameText = itemView.findViewById(R.id.foodNameText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}
