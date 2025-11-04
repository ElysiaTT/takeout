package com.example.test;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private static ShoppingCart instance;
    private List<CartItem> items;

    private ShoppingCart() {
        items = new ArrayList<>();
    }

    public static ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public void addItem(FoodItem foodItem) {
        for (CartItem item : items) {
            if (item.getFoodItem().getName().equals(foodItem.getName())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        items.add(new CartItem(foodItem, 1));
    }

    public void removeItem(FoodItem foodItem) {
        items.removeIf(item -> item.getFoodItem().getName().equals(foodItem.getName()));
    }

    public void updateQuantity(FoodItem foodItem, int quantity) {
        for (CartItem item : items) {
            if (item.getFoodItem().getName().equals(foodItem.getName())) {
                if (quantity <= 0) {
                    removeItem(foodItem);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clear() {
        items.clear();
    }
}
