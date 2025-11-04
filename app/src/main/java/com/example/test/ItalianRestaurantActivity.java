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

public class ItalianRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;

    private TextView catPizza, catPasta, catAppetizer, catDessert, catWine;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_italian_restaurant);

        shoppingCart = ShoppingCart.getInstance();

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
        allFoodItems = new ArrayList<>();

        // 披萨
        allFoodItems.add(new FoodItem("玛格丽特披萨 🏆",
            "意式薄底，圣马扎诺番茄酱与水牛马苏里拉奶酪的经典组合。",
            68.00, "披萨"));

        allFoodItems.add(new FoodItem("四季披萨",
            "四种口味的完美结合：火腿、蘑菇、朝鲜蓟、橄榄。",
            78.00, "披萨"));

        allFoodItems.add(new FoodItem("海鲜披萨",
            "鲜虾、青口贝、鱿鱼等新鲜海鲜，海洋的味道。",
            88.00, "披萨"));

        allFoodItems.add(new FoodItem("意式辣肠披萨",
            "经典辣肠片配番茄酱和马苏里拉奶酪。🌶️",
            75.00, "披萨"));

        // 意面
        allFoodItems.add(new FoodItem("海鲜意面",
            "新鲜海鲜与番茄汁翻炒，意面充分吸收汤汁精华。",
            85.00, "意面"));

        allFoodItems.add(new FoodItem("肉酱意面",
            "经典博洛尼亚肉酱，慢火熬制3小时。",
            58.00, "意面"));

        allFoodItems.add(new FoodItem("奶油培根意面",
            "意式培根与奶油的完美融合，口感丰富。",
            65.00, "意面"));

        allFoodItems.add(new FoodItem("松露野菇意面",
            "黑松露与多种野生蘑菇，奢华美味。",
            128.00, "意面"));

        // 前菜沙拉
        allFoodItems.add(new FoodItem("凯撒沙拉",
            "新鲜罗马生菜，凯撒酱汁，帕玛森芝士碎。",
            38.00, "前菜沙拉"));

        allFoodItems.add(new FoodItem("意式火腿拼盘",
            "帕尔马火腿、意式萨拉米、橄榄、芝士。",
            88.00, "前菜沙拉"));

        allFoodItems.add(new FoodItem("卡布里沙拉",
            "番茄、水牛芝士、罗勒，意大利国旗色。",
            45.00, "前菜沙拉"));

        allFoodItems.add(new FoodItem("烤蔬菜拼盘",
            "时令蔬菜橄榄油烤制，健康美味。",
            42.00, "前菜沙拉"));

        // 甜品
        allFoodItems.add(new FoodItem("提拉米苏 🏆",
            "马斯卡彭奶酪与咖啡酒手指饼干的完美融合。",
            38.00, "甜品"));

        allFoodItems.add(new FoodItem("意式奶冻",
            "奶香浓郁，口感细腻，配水果酱。",
            32.00, "甜品"));

        allFoodItems.add(new FoodItem("西西里卷",
            "酥脆外壳，香浓奶油馅料。",
            35.00, "甜品"));

        // 葡萄酒
        allFoodItems.add(new FoodItem("基安蒂红葡萄酒",
            "托斯卡纳经典，适合搭配披萨和意面。",
            188.00, "葡萄酒"));

        allFoodItems.add(new FoodItem("普罗塞克起泡酒",
            "清爽怡人，适合开胃。",
            158.00, "葡萄酒"));

        allFoodItems.add(new FoodItem("意式柠檬酒",
            "餐后酒，清新解腻。",
            58.00, "葡萄酒"));

        currentFoodItems = new ArrayList<>(allFoodItems);
    }

    private void setupRecyclerView() {
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemAdapter = new FoodItemAdapter(currentFoodItems, foodItem -> {
            shoppingCart.addItem(foodItem);
            Toast.makeText(ItalianRestaurantActivity.this,
                foodItem.getName() + " aggiunto al carrello!",
                Toast.LENGTH_SHORT).show();
            updateCartButton();
        });
        foodRecyclerView.setAdapter(foodItemAdapter);
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
