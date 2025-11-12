package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItalianRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;
    private FoodDatabaseHelper databaseHelper;

    private TextView catPizza, catPasta, catAppetizer, catDessert, catWine;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_italian_restaurant);

        shoppingCart = ShoppingCart.getInstance();
        databaseHelper = new FoodDatabaseHelper(this);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        cartButton = findViewById(R.id.cartButton);

        // 分类导航
        catPizza = findViewById(R.id.catPizza);
        catPasta = findViewById(R.id.catPasta);
        catAppetizer = findViewById(R.id.catAppetizer);
        catDessert = findViewById(R.id.catDessert);
        catWine = findViewById(R.id.catWine);

        currentSelectedCategory = catPizza;

        setupFoodItems();
        setupRecyclerView();
        setupCategoryNavigation();
        updateCartButton();

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ItalianRestaurantActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // 默认显示披萨
        filterByCategory("披萨");
    }

    private void setupFoodItems() {
        try {
            Log.d("ItalianRestaurant", "Starting setupFoodItems()");
            // 从数据库获取所有菜品
            allFoodItems = databaseHelper.getAllFoodItems(FoodDatabaseHelper.TABLE_ITALIAN_FOOD);
            Log.d("ItalianRestaurant", "Total food items retrieved: " + (allFoodItems != null ? allFoodItems.size() : "null"));
            
            // 初始化currentFoodItems
            currentFoodItems = new ArrayList<>();
            
            // 确保foodItemAdapter已初始化
            if (foodItemAdapter == null) {
                setupRecyclerView();
                Log.d("ItalianRestaurant", "Food adapter initialized");
            }
            
            // 默认显示"披萨"分类
            if (allFoodItems != null && !allFoodItems.isEmpty()) {
                Log.d("ItalianRestaurant", "Getting food items for category: 披萨");
                filterByCategory("披萨");
            }
        } catch (Exception e) {
            Log.e("ItalianRestaurant", "Error setting up food items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        Log.d("ItalianRestaurant", "Setting up RecyclerView");
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // 确保currentFoodItems不为空
        if (currentFoodItems == null) {
            currentFoodItems = new ArrayList<>();
        }
        
        // 使用匿名内部类实现OnItemClickListener
        foodItemAdapter = new FoodItemAdapter(currentFoodItems, new FoodItemAdapter.OnItemClickListener() {
            @Override
            public void onAddToCart(FoodItem foodItem) {
                // 添加到购物车逻辑
                shoppingCart.addItem(foodItem);
                updateCartButton();
                // 显示添加成功提示
                Toast.makeText(ItalianRestaurantActivity.this, foodItem.getName() + " 已添加到购物车", Toast.LENGTH_SHORT).show();
            }
        });
        
        foodRecyclerView.setAdapter(foodItemAdapter);
        Log.d("ItalianRestaurant", "RecyclerView setup completed");
    }

    private void setupCategoryNavigation() {
        catPizza.setOnClickListener(v -> {
            filterByCategory("披萨");
            updateCategoryUI(catPizza);
        });

        catPasta.setOnClickListener(v -> {
            filterByCategory("意面");
            updateCategoryUI(catPasta);
        });

        catAppetizer.setOnClickListener(v -> {
            filterByCategory("前菜沙拉");
            updateCategoryUI(catAppetizer);
        });

        catDessert.setOnClickListener(v -> {
            filterByCategory("甜品");
            updateCategoryUI(catDessert);
        });

        catWine.setOnClickListener(v -> {
            filterByCategory("葡萄酒");
            updateCategoryUI(catWine);
        });
    }

    private void filterByCategory(String category) {
        Log.d("ItalianRestaurant", "Filtering by category: " + category);
        // 从数据库根据分类获取菜品
        currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_ITALIAN_FOOD, category);
        
        // 确保currentFoodItems不为空
        if (currentFoodItems == null) {
            currentFoodItems = new ArrayList<>();
            Log.d("ItalianRestaurant", "currentFoodItems initialized as empty list");
        }
        
        Log.d("ItalianRestaurant", "Filtered items count: " + currentFoodItems.size());
        
        // 更新适配器数据并通知变化
            if (foodItemAdapter != null) {
                foodItemAdapter.updateData(currentFoodItems);
                foodItemAdapter.notifyDataSetChanged();
                Log.d("ItalianRestaurant", "Adapter data updated and notified");
            } else {
                Log.e("ItalianRestaurant", "foodItemAdapter is null");
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void updateCategoryUI(TextView selectedCategory) {
        // 重置所有分类样式
        catPizza.setBackgroundColor(Color.parseColor("#EEE8DC"));
        catPizza.setTextColor(Color.parseColor("#666666"));
        catPasta.setBackgroundColor(Color.parseColor("#EEE8DC"));
        catPasta.setTextColor(Color.parseColor("#666666"));
        catAppetizer.setBackgroundColor(Color.parseColor("#EEE8DC"));
        catAppetizer.setTextColor(Color.parseColor("#666666"));
        catDessert.setBackgroundColor(Color.parseColor("#EEE8DC"));
        catDessert.setTextColor(Color.parseColor("#666666"));
        catWine.setBackgroundColor(Color.parseColor("#EEE8DC"));
        catWine.setTextColor(Color.parseColor("#666666"));

        // 高亮选中的分类
        selectedCategory.setBackgroundColor(Color.parseColor("#6B8E23"));
        selectedCategory.setTextColor(Color.parseColor("#FFFFFF"));
        currentSelectedCategory = selectedCategory;
    }

    private void updateCartButton() {
        int itemCount = shoppingCart.getItemCount();
        double total = shoppingCart.getTotalPrice();
        cartButton.setText(String.format(Locale.US,
            "🛒 购物车 (%d items) - ¥%.2f", itemCount, total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButton();
    }
}
