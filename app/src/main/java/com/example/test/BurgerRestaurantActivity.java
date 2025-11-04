package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    private TextView catClassicBurger, catSpecialBurger, catSnacks, catShakes;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger_restaurant);

        shoppingCart = ShoppingCart.getInstance();

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
        allFoodItems = new ArrayList<>();

        // 经典汉堡
        allFoodItems.add(new FoodItem("经典芝士牛肉汉堡 🏆",
            "100%纯牛肉饼，搭配融化的车打芝士和新鲜蔬菜。",
            32.00, "经典汉堡"));

        allFoodItems.add(new FoodItem("双层牛肉汉堡",
            "双倍牛肉饼，双倍满足感！",
            45.00, "经典汉堡"));

        allFoodItems.add(new FoodItem("培根汉堡",
            "酥脆培根配多汁牛肉饼，绝配！",
            38.00, "经典汉堡"));

        allFoodItems.add(new FoodItem("鸡肉汉堡",
            "炸鸡排配生菜番茄，清爽美味。",
            28.00, "经典汉堡"));

        // 特制汉堡
        allFoodItems.add(new FoodItem("闪电特级汉堡",
            "三层牛肉饼！芝士、培根、洋葱圈全都有！",
            68.00, "特制汉堡"));

        allFoodItems.add(new FoodItem("墨西哥辣堡",
            "墨西哥辣椒、芝士、莎莎酱，火辣过瘾！🌶️🌶️",
            42.00, "特制汉堡"));

        allFoodItems.add(new FoodItem("蘑菇瑞士汉堡",
            "蘑菇配瑞士芝士，口感浓郁。",
            48.00, "特制汉堡"));

        allFoodItems.add(new FoodItem("BBQ汉堡",
            "BBQ酱配洋葱圈，美式风味十足。",
            45.00, "特制汉堡"));

        allFoodItems.add(new FoodItem("素食汉堡",
            "植物肉饼，健康环保不失美味。",
            38.00, "特制汉堡"));

        // 小食拼盘
        allFoodItems.add(new FoodItem("炸鸡桶",
            "外皮酥脆，鸡肉鲜嫩多汁。6块装。",
            48.00, "小食拼盘"));

        allFoodItems.add(new FoodItem("鸡块拼盘",
            "金黄酥脆的鸡块，配多种酱料。10块装。",
            32.00, "小食拼盘"));

        allFoodItems.add(new FoodItem("薯条（大份）",
            "超大份金黄薯条，外酥内软。",
            18.00, "小食拼盘"));

        allFoodItems.add(new FoodItem("洋葱圈",
            "香脆洋葱圈，停不下来的美味。",
            22.00, "小食拼盘"));

        allFoodItems.add(new FoodItem("鸡翅拼盘",
            "烤鸡翅6只，配蜂蜜芥末酱。",
            35.00, "小食拼盘"));

        allFoodItems.add(new FoodItem("芝士薯条",
            "薯条上淋满浓郁芝士酱。",
            25.00, "小食拼盘"));

        // 奶昔冰沙
        allFoodItems.add(new FoodItem("巧克力奶昔 🏆",
            "冰爽绵密，浓郁巧克力风味。",
            22.00, "奶昔冰沙"));

        allFoodItems.add(new FoodItem("香草奶昔",
            "经典香草口味，清甜顺滑。",
            20.00, "奶昔冰沙"));

        allFoodItems.add(new FoodItem("草莓奶昔",
            "新鲜草莓制作，果香浓郁。",
            22.00, "奶昔冰沙"));

        allFoodItems.add(new FoodItem("奥利奥奶昔",
            "奥利奥饼干碎配冰淇淋，香甜可口。",
            25.00, "奶昔冰沙"));

        allFoodItems.add(new FoodItem("芒果冰沙",
            "热带芒果风味，清凉解暑。",
            20.00, "奶昔冰沙"));

        allFoodItems.add(new FoodItem("可乐（大杯）",
            "冰镇可口可乐，畅爽无比。",
            12.00, "奶昔冰沙"));

        currentFoodItems = new ArrayList<>(allFoodItems);
    }

    private void setupRecyclerView() {
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemAdapter = new FoodItemAdapter(currentFoodItems, foodItem -> {
            shoppingCart.addItem(foodItem);
            Toast.makeText(BurgerRestaurantActivity.this,
                foodItem.getName() + " 已加入购物车！",
                Toast.LENGTH_SHORT).show();
            updateCartButton();
        });
        foodRecyclerView.setAdapter(foodItemAdapter);
    }

    private void setupCategoryNavigation() {
        catClassicBurger.setOnClickListener(v -> {
            filterByCategory("经典汉堡");
            updateCategoryUI(catClassicBurger);
        });

        catSpecialBurger.setOnClickListener(v -> {
            filterByCategory("特制汉堡");
            updateCategoryUI(catSpecialBurger);
        });

        catSnacks.setOnClickListener(v -> {
            filterByCategory("小食拼盘");
            updateCategoryUI(catSnacks);
        });

        catShakes.setOnClickListener(v -> {
            filterByCategory("奶昔冰沙");
            updateCategoryUI(catShakes);
        });
    }

    private void filterByCategory(String category) {
        currentFoodItems.clear();
        for (FoodItem item : allFoodItems) {
            if (item.getCategory().equals(category)) {
                currentFoodItems.add(item);
            }
        }
        foodItemAdapter.notifyDataSetChanged();
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
