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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.nameText.setText(item.getFoodItem().getName());
        holder.priceText.setText(String.format(Locale.US, "$%.2f", item.getFoodItem().getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        holder.totalText.setText(String.format(Locale.US, "$%.2f", item.getTotalPrice()));

        holder.increaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView priceText;
        TextView quantityText;
        TextView totalText;
        Button increaseButton;
        Button decreaseButton;
        Button removeButton;

        CartViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.cartItemName);
            priceText = itemView.findViewById(R.id.cartItemPrice);
            quantityText = itemView.findViewById(R.id.cartItemQuantity);
            totalText = itemView.findViewById(R.id.cartItemTotal);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
