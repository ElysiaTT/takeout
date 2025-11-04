package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> foodItems;
    private ShoppingCart shoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shoppingCart = ShoppingCart.getInstance();

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        cartButton = findViewById(R.id.cartButton);

        setupFoodItems();
        setupRecyclerView();
        updateCartButton();

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void setupFoodItems() {
        foodItems = new ArrayList<>();

        // Burgers
        foodItems.add(new FoodItem("Classic Burger", "Beef patty with lettuce, tomato, and cheese", 8.99, "Burgers"));
        foodItems.add(new FoodItem("Bacon Cheeseburger", "Double beef patty with bacon and cheese", 11.99, "Burgers"));
        foodItems.add(new FoodItem("Veggie Burger", "Plant-based patty with fresh vegetables", 9.99, "Burgers"));

        // Pizza
        foodItems.add(new FoodItem("Margherita Pizza", "Fresh mozzarella, tomato sauce, and basil", 12.99, "Pizza"));
        foodItems.add(new FoodItem("Pepperoni Pizza", "Classic pepperoni with mozzarella cheese", 13.99, "Pizza"));
        foodItems.add(new FoodItem("Supreme Pizza", "Loaded with meats and vegetables", 15.99, "Pizza"));

        // Pasta
        foodItems.add(new FoodItem("Spaghetti Carbonara", "Creamy pasta with bacon and parmesan", 10.99, "Pasta"));
        foodItems.add(new FoodItem("Penne Arrabiata", "Spicy tomato sauce with penne pasta", 9.99, "Pasta"));
        foodItems.add(new FoodItem("Fettuccine Alfredo", "Rich cream sauce with fettuccine", 11.99, "Pasta"));

        // Asian
        foodItems.add(new FoodItem("Chicken Fried Rice", "Wok-fried rice with chicken and vegetables", 9.99, "Asian"));
        foodItems.add(new FoodItem("Pad Thai", "Thai rice noodles with shrimp and peanuts", 12.99, "Asian"));
        foodItems.add(new FoodItem("Sushi Roll Combo", "Assorted sushi rolls with wasabi and ginger", 16.99, "Asian"));

        // Sides
        foodItems.add(new FoodItem("French Fries", "Crispy golden fries", 3.99, "Sides"));
        foodItems.add(new FoodItem("Onion Rings", "Beer-battered onion rings", 4.99, "Sides"));
        foodItems.add(new FoodItem("Caesar Salad", "Fresh romaine with caesar dressing", 5.99, "Sides"));

        // Drinks
        foodItems.add(new FoodItem("Soft Drink", "Coke, Sprite, or Fanta", 1.99, "Drinks"));
        foodItems.add(new FoodItem("Fresh Juice", "Orange, apple, or mixed berry", 3.99, "Drinks"));
        foodItems.add(new FoodItem("Milkshake", "Chocolate, vanilla, or strawberry", 4.99, "Drinks"));
    }

    private void setupRecyclerView() {
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemAdapter = new FoodItemAdapter(foodItems, foodItem -> {
            shoppingCart.addItem(foodItem);
            Toast.makeText(MainActivity.this,
                foodItem.getName() + " added to cart!",
                Toast.LENGTH_SHORT).show();
            updateCartButton();
        });
        foodRecyclerView.setAdapter(foodItemAdapter);
    }

    private void updateCartButton() {
        int itemCount = shoppingCart.getItemCount();
        double total = shoppingCart.getTotalPrice();
        cartButton.setText(String.format(Locale.US,
            "View Cart (%d items) - $%.2f", itemCount, total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }
}