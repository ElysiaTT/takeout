package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChineseRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;
    private FoodDatabaseHelper databaseHelper;

    private TextView catSignature, catSpicy, catMainDish, catSoup, catDrink;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese_restaurant);

        shoppingCart = ShoppingCart.getInstance();
        databaseHelper = new FoodDatabaseHelper(this);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        cartButton = findViewById(R.id.cartButton);

        // 分类导航
        catSignature = findViewById(R.id.catSignature);
        catSpicy = findViewById(R.id.catSpicy);
        catMainDish = findViewById(R.id.catMainDish);
        catSoup = findViewById(R.id.catSoup);
        catDrink = findViewById(R.id.catDrink);

        currentSelectedCategory = catSignature;

        setupFoodItems();
        setupRecyclerView();
        setupCategoryNavigation();
        updateCartButton();

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChineseRestaurantActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // 默认显示招牌推荐
        filterByCategory("招牌推荐");
    }

    private void setupFoodItems() {
        try {
            // 从数据库获取所有菜品
            Log.d("ChineseRestaurant", "Starting setupFoodItems()");
            allFoodItems = databaseHelper.getAllFoodItems(FoodDatabaseHelper.TABLE_CHINESE_FOOD);
            Log.d("ChineseRestaurant", "Total food items retrieved: " + (allFoodItems != null ? allFoodItems.size() : "null"));
            
            // 初始化currentFoodItems
            currentFoodItems = new ArrayList<>();
            
            // 确保foodItemAdapter已初始化
            if (foodItemAdapter == null) {
                foodItemAdapter = new FoodItemAdapter(currentFoodItems, foodItem -> {
                    shoppingCart.addItem(foodItem);
                    Toast.makeText(ChineseRestaurantActivity.this,
                        foodItem.getName() + " 已加入购物车！",
                        Toast.LENGTH_SHORT).show();
                    updateCartButton();
                });
                foodRecyclerView.setAdapter(foodItemAdapter);
                Log.d("ChineseRestaurant", "Food adapter initialized");
            }
            
            // 直接从数据库获取招牌推荐分类的菜品
            Log.d("ChineseRestaurant", "Getting food items for category: 招牌推荐");
            currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_CHINESE_FOOD, "招牌推荐");
            
            // 确保currentFoodItems不为空
            if (currentFoodItems == null) {
                currentFoodItems = new ArrayList<>();
                Log.d("ChineseRestaurant", "currentFoodItems initialized as empty list");
            }
            
            Log.d("ChineseRestaurant", "Filtered items count: " + currentFoodItems.size());
            
            // 更新适配器数据
            foodItemAdapter.updateData(currentFoodItems);
            foodItemAdapter.notifyDataSetChanged();
            Log.d("ChineseRestaurant", "Adapter data updated and notified");
            
        } catch (Exception e) {
            Log.e("ChineseRestaurant", "Error setting up food items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemAdapter = new FoodItemAdapter(currentFoodItems, foodItem -> {
            shoppingCart.addItem(foodItem);
            Toast.makeText(ChineseRestaurantActivity.this,
                foodItem.getName() + " 已加入购物车！",
                Toast.LENGTH_SHORT).show();
            updateCartButton();
        });
        foodRecyclerView.setAdapter(foodItemAdapter);
    }

    private void setupCategoryNavigation() {
        catSignature.setOnClickListener(v -> {
            filterByCategory("招牌推荐");
            updateCategoryUI(catSignature);
        });

        catSpicy.setOnClickListener(v -> {
            filterByCategory("川湘风味");
            updateCategoryUI(catSpicy);
        });

        catMainDish.setOnClickListener(v -> {
            filterByCategory("主食");
            updateCategoryUI(catMainDish);
        });

        catSoup.setOnClickListener(v -> {
            filterByCategory("汤羹");
            updateCategoryUI(catSoup);
        });

        catDrink.setOnClickListener(v -> {
            filterByCategory("饮品");
            updateCategoryUI(catDrink);
        });
    }

    private void filterByCategory(String category) {
        Log.d("ChineseRestaurant", "Filtering by category: " + category);
        // 从数据库根据分类获取菜品
        currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_CHINESE_FOOD, category);
        
        // 确保currentFoodItems不为空
        if (currentFoodItems == null) {
            Log.d("ChineseRestaurant", "currentFoodItems initialized as empty list");
            currentFoodItems = new ArrayList<>();
        }
        
        Log.d("ChineseRestaurant", "Filtered items count: " + currentFoodItems.size());
        
        // 更新适配器数据并通知变化
        if (foodItemAdapter != null) {
            foodItemAdapter.updateData(currentFoodItems);
            foodItemAdapter.notifyDataSetChanged();
            Log.d("ChineseRestaurant", "Adapter data updated and notified");
        } else {
            Log.e("ChineseRestaurant", "foodItemAdapter is null");
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
        catSignature.setBackgroundColor(Color.parseColor("#FFF0F0"));
        catSignature.setTextColor(Color.parseColor("#666666"));
        catSpicy.setBackgroundColor(Color.parseColor("#FFF0F0"));
        catSpicy.setTextColor(Color.parseColor("#666666"));
        catMainDish.setBackgroundColor(Color.parseColor("#FFF0F0"));
        catMainDish.setTextColor(Color.parseColor("#666666"));
        catSoup.setBackgroundColor(Color.parseColor("#FFF0F0"));
        catSoup.setTextColor(Color.parseColor("#666666"));
        catDrink.setBackgroundColor(Color.parseColor("#FFF0F0"));
        catDrink.setTextColor(Color.parseColor("#666666"));

        // 高亮选中的分类
        selectedCategory.setBackgroundColor(Color.parseColor("#DC143C"));
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
