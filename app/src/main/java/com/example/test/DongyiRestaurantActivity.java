package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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
import com.example.test.FoodItem;

public class DongyiRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;
    private FoodDatabaseHelper databaseHelper;

    private TextView catNoodles, catMeals, catSteamedFried;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongyi_restaurant);

        shoppingCart = ShoppingCart.getInstance();
        databaseHelper = new FoodDatabaseHelper(this);

        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        cartButton = findViewById(R.id.cartButton);

        // 分类导航
        catNoodles = findViewById(R.id.catNoodles);
        catMeals = findViewById(R.id.catMeals);
        catSteamedFried = findViewById(R.id.catSteamedFried);

        currentSelectedCategory = catNoodles;

        setupFoodItems();
        setupRecyclerView();
        setupCategoryNavigation();
        updateCartButton();

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(DongyiRestaurantActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // 默认显示面食系列
        filterByCategory("面食系列");
        

    }

    private void setupFoodItems() {
        try {
            Log.d("DongyiRestaurant", "Starting setupFoodItems()");
            // 从数据库获取所有菜品
            allFoodItems = databaseHelper.getAllFoodItems(FoodDatabaseHelper.TABLE_DONGYI_FOOD);
            Log.d("DongyiRestaurant", "Total food items retrieved: " + (allFoodItems != null ? allFoodItems.size() : "null"));
            
            // 初始化currentFoodItems
            currentFoodItems = new ArrayList<>();
            
            // 确保foodItemAdapter已初始化
            if (foodItemAdapter == null) {
                setupRecyclerView();
                Log.d("DongyiRestaurant", "Food adapter initialized");
            }
            
            // 默认显示第一个分类的菜品
            if (allFoodItems != null && !allFoodItems.isEmpty()) {
                // 默认显示"面食系列"分类
                Log.d("DongyiRestaurant", "Getting food items for category: 面食系列");
                currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_DONGYI_FOOD, "面食系列");
                
                // 确保currentFoodItems不为空
                if (currentFoodItems == null) {
                    currentFoodItems = new ArrayList<>();
                    Log.d("DongyiRestaurant", "currentFoodItems initialized as empty list");
                }
                
                // 更新适配器数据
                if (foodItemAdapter != null) {
                    foodItemAdapter.updateData(currentFoodItems);
                    foodItemAdapter.notifyDataSetChanged();
                    Log.d("DongyiRestaurant", "Adapter data updated and notified");
                }
            }
        } catch (Exception e) {
            Log.e("DongyiRestaurant", "Error setting up food items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        Log.d("DongyiRestaurant", "Setting up RecyclerView");
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
                Toast.makeText(DongyiRestaurantActivity.this, foodItem.getName() + " 已添加到购物车", Toast.LENGTH_SHORT).show();
            }
        });
        
        foodRecyclerView.setAdapter(foodItemAdapter);
        Log.d("DongyiRestaurant", "RecyclerView setup completed");
    }

    private void setupCategoryNavigation() {
        catNoodles.setOnClickListener(v -> {
            filterByCategory("面食系列");
            updateCategoryUI(catNoodles);
        });

        catMeals.setOnClickListener(v -> {
            filterByCategory("套餐系列");
            updateCategoryUI(catMeals);
        });

        catSteamedFried.setOnClickListener(v -> {
            filterByCategory("蒸炸系列");
            updateCategoryUI(catSteamedFried);
        });
    }

    private void refreshFoodData() {
        Log.d("DongyiRestaurant", "refreshFoodData: 开始刷新数据 (时间戳: " + System.currentTimeMillis() + ")");
        try {
            // 1. 记录当前选中的分类
            String currentCategory = "面食系列"; // 默认分类
            if (currentSelectedCategory != null) {
                currentCategory = currentSelectedCategory.getText().toString();
                Log.d("DongyiRestaurant", "当前选中的分类: " + currentCategory);
            }
            
            // 2. 直接从JSON加载数据，不依赖任何缓存
            Log.d("DongyiRestaurant", "调用loadFoodFromJsonDirectly直接从JSON加载最新数据");
            allFoodItems = databaseHelper.loadFoodFromJsonDirectly(FoodDatabaseHelper.TABLE_DONGYI_FOOD, "foods/dongyi_foods.json");
            Log.d("DongyiRestaurant", "成功直接从JSON加载" + allFoodItems.size() + "条菜品数据");
            
            // 4. 记录菜品价格用于调试
            if (allFoodItems != null && !allFoodItems.isEmpty()) {
                for (FoodItem item : allFoodItems) {
                    if (item.getName().contains("川味担担面")) {
                        Log.d("DongyiRestaurant", "[川味担担面!] 当前价格: " + item.getPrice());
                    }
                    if (item.getName().contains("武汉热干面")) {
                        Log.d("DongyiRestaurant", "[武汉热干面!] 当前价格: " + item.getPrice());
                    }
                }
            }
            
            // 5. 根据当前分类筛选并刷新UI
            Log.d("DongyiRestaurant", "根据分类" + currentCategory + "筛选菜品");
            if (currentCategory.equals("面食系列") || currentCategory.equals("套餐系列") || currentCategory.equals("蒸炸系列")) {
                filterByCategory(currentCategory);
            } else {
                filterByCategory("面食系列");
            }
            
            // 6. 刷新RecyclerView
            Log.d("DongyiRestaurant", "刷新RecyclerView");
            if (foodItemAdapter != null) {
                foodItemAdapter.notifyDataSetChanged();
                Log.d("DongyiRestaurant", "RecyclerView已刷新");
            }
            
            Log.d("DongyiRestaurant", "refreshFoodData: 数据刷新完成 (时间戳: " + System.currentTimeMillis() + ")");
            Toast.makeText(this, "数据已更新，请检查菜品价格", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e("DongyiRestaurant", "刷新数据时出错: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "刷新失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void filterByCategory(String category) {
        Log.d("DongyiRestaurant", "Filtering by category: " + category);
        // 从数据库根据分类获取菜品
        currentFoodItems = databaseHelper.getFoodItemsByCategory(FoodDatabaseHelper.TABLE_DONGYI_FOOD, category);
        
        // 确保currentFoodItems不为空
        if (currentFoodItems == null) {
            currentFoodItems = new ArrayList<>();
            Log.d("DongyiRestaurant", "currentFoodItems initialized as empty list");
        }
        
        Log.d("DongyiRestaurant", "Filtered items count: " + currentFoodItems.size());
        
        // 更新适配器数据并通知变化
        if (foodItemAdapter != null) {
            foodItemAdapter.updateData(currentFoodItems);
            foodItemAdapter.notifyDataSetChanged();
            Log.d("DongyiRestaurant", "Adapter data updated and notified");
        } else {
            Log.e("DongyiRestaurant", "foodItemAdapter is null");
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
        catNoodles.setBackgroundColor(Color.parseColor("#FFE4B5"));
        catNoodles.setTextColor(Color.parseColor("#666666"));
        catMeals.setBackgroundColor(Color.parseColor("#FFE4B5"));
        catMeals.setTextColor(Color.parseColor("#666666"));
        catSteamedFried.setBackgroundColor(Color.parseColor("#FFE4B5"));
        catSteamedFried.setTextColor(Color.parseColor("#666666"));

        // 高亮选中的分类
        selectedCategory.setBackgroundColor(Color.parseColor("#FF8C00"));
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
        Log.d("DongyiRestaurant", "onResume: 界面可见，直接从JSON重新加载最新数据");
        
        // 更新购物车按钮状态
        updateCartButton();
        
        // 每次进入界面都直接调用refreshFoodData方法
        refreshFoodData();
        Log.d("DongyiRestaurant", "onResume: 数据刷新完成");
    }
}
