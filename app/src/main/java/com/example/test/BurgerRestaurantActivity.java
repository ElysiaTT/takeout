package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BurgerRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;
    private FoodDatabaseHelper databaseHelper;

    private TextView catClassicBurger, catSpecialBurger, catSnacks, catShakes;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger_restaurant);

        shoppingCart = ShoppingCart.getInstance();
        databaseHelper = new FoodDatabaseHelper(this);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        cartButton = findViewById(R.id.cartButton);

        // 分类导航
        catClassicBurger = findViewById(R.id.catClassicBurger);
        catSpecialBurger = findViewById(R.id.catSpecialBurger);
        catSnacks = findViewById(R.id.catSnacks);
        catShakes = findViewById(R.id.catShakes);

        currentSelectedCategory = catClassicBurger;

        setupFoodItems();
        setupRecyclerView();
        setupCategoryNavigation();
        updateCartButton();

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(BurgerRestaurantActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // 默认显示经典汉堡
        filterByCategory("经典汉堡");
    }

    private void setupFoodItems() {
        try {
            Log.d("BurgerRestaurant", "Starting setupFoodItems()");
            // 从数据库获取所有菜品
            allFoodItems = databaseHelper.getAllFoodItems(FoodDatabaseHelper.TABLE_BURGER_FOOD);
            Log.d("BurgerRestaurant", "Total food items retrieved: " + (allFoodItems != null ? allFoodItems.size() : "null"));
            
            // 初始化currentFoodItems
            currentFoodItems = new ArrayList<>();
            
            // 确保foodItemAdapter已初始化
            if (foodItemAdapter == null) {
                setupRecyclerView();
                Log.d("BurgerRestaurant", "Food adapter initialized");
            }
            
            // 直接从数据库获取经典汉堡分类的菜品
            Log.d("BurgerRestaurant", "Getting food items for category: 经典汉堡");
            currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_BURGER_FOOD, "经典汉堡");
            
            // 确保currentFoodItems不为空
            if (currentFoodItems == null) {
                currentFoodItems = new ArrayList<>();
                Log.d("BurgerRestaurant", "currentFoodItems initialized as empty list");
            }
            
            Log.d("BurgerRestaurant", "Filtered items count: " + currentFoodItems.size());
            
            // 更新适配器数据
            if (foodItemAdapter != null) {
                foodItemAdapter.updateData(currentFoodItems);
                foodItemAdapter.notifyDataSetChanged();
                Log.d("BurgerRestaurant", "Adapter data updated and notified");
            }
            
        } catch (Exception e) {
            Log.e("BurgerRestaurant", "Error setting up food items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        Log.d("BurgerRestaurant", "Setting up RecyclerView");
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
                Toast.makeText(BurgerRestaurantActivity.this, foodItem.getName() + " 已添加到购物车", Toast.LENGTH_SHORT).show();
            }
        });
        
        foodRecyclerView.setAdapter(foodItemAdapter);
        Log.d("BurgerRestaurant", "RecyclerView setup completed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private void setupCategoryNavigation() {
        catClassicBurger.setOnClickListener(v -> {
            filterByCategory("经典汉堡");
            updateCategoryUI(catClassicBurger);
        });

        catSpecialBurger.setOnClickListener(v -> {
            filterByCategory("特色汉堡");
            updateCategoryUI(catSpecialBurger);
        });

        catSnacks.setOnClickListener(v -> {
            filterByCategory("小食");
            updateCategoryUI(catSnacks);
        });

        catShakes.setOnClickListener(v -> {
            filterByCategory("奶昔冰沙");
            updateCategoryUI(catShakes);
        });
    }

    private void filterByCategory(String category) {
        Log.d("BurgerRestaurant", "Filtering by category: " + category);
        // 从数据库根据分类获取菜品
        currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_BURGER_FOOD, category);
        
        // 确保currentFoodItems不为空
        if (currentFoodItems == null) {
            currentFoodItems = new ArrayList<>();
            Log.d("BurgerRestaurant", "currentFoodItems initialized as empty list");
        }
        
        Log.d("BurgerRestaurant", "Filtered items count: " + currentFoodItems.size());
        
        // 更新适配器数据并通知变化
        if (foodItemAdapter != null) {
            foodItemAdapter.updateData(currentFoodItems);
            foodItemAdapter.notifyDataSetChanged();
            Log.d("BurgerRestaurant", "Adapter data updated and notified");
        } else {
            Log.e("BurgerRestaurant", "foodItemAdapter is null");
        }
    }

    private void updateCategoryUI(TextView selectedCategory) {
        // 重置所有分类样式
        catClassicBurger.setBackgroundColor(Color.parseColor("#CCE6FF"));
        catClassicBurger.setTextColor(Color.parseColor("#666666"));
        catSpecialBurger.setBackgroundColor(Color.parseColor("#CCE6FF"));
        catSpecialBurger.setTextColor(Color.parseColor("#666666"));
        catSnacks.setBackgroundColor(Color.parseColor("#CCE6FF"));
        catSnacks.setTextColor(Color.parseColor("#666666"));
        catShakes.setBackgroundColor(Color.parseColor("#CCE6FF"));
        catShakes.setTextColor(Color.parseColor("#666666"));

        // 高亮选中的分类
        selectedCategory.setBackgroundColor(Color.parseColor("#1E90FF"));
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
