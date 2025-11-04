package com.example.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class ChineseRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;

    private TextView catSignature, catSpicy, catMainDish, catSoup, catDrink;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese_restaurant);

        shoppingCart = ShoppingCart.getInstance();

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
        allFoodItems = new ArrayList<>();

        // 招牌推荐
        FoodItem pekingDuck = new FoodItem("北京烤鸭",
            "果木挂炉烤制，外皮酥香，肉质鲜嫩。搭配全套饼酱。",
            128.00, "招牌推荐");
        allFoodItems.add(pekingDuck);

        allFoodItems.add(new FoodItem("小笼汤包",
            "皮薄馅大，汤汁饱满，请小心烫口。",
            25.00, "招牌推荐"));

        allFoodItems.add(new FoodItem("东坡肉",
            "肥而不腻，入口即化的经典杭州菜。",
            68.00, "招牌推荐"));

        // 川湘风味
        allFoodItems.add(new FoodItem("麻婆豆腐",
            "传统川味，麻辣鲜香，下饭神器。🌶️",
            32.00, "川湘风味"));

        allFoodItems.add(new FoodItem("水煮鱼",
            "鲜嫩鱼片，麻辣鲜香，配菜丰富。🌶️🌶️",
            88.00, "川湘风味"));

        allFoodItems.add(new FoodItem("剁椒鱼头",
            "湘菜名品，鲜辣开胃，鱼肉细嫩。🌶️🌶️",
            98.00, "川湘风味"));

        allFoodItems.add(new FoodItem("宫保鸡丁",
            "酸甜微辣，鸡肉嫩滑，花生酥脆。",
            38.00, "川湘风味"));

        // 主食
        allFoodItems.add(new FoodItem("扬州炒饭",
            "粒粒分明，配料丰富，色香味俱全。",
            28.00, "主食"));

        allFoodItems.add(new FoodItem("担担面",
            "四川特色面食，麻辣鲜香。🌶️",
            22.00, "主食"));

        allFoodItems.add(new FoodItem("馄饨",
            "皮薄馅嫩，汤清味美。",
            20.00, "主食"));

        allFoodItems.add(new FoodItem("葱油拌面",
            "简单美味，葱香浓郁。",
            18.00, "主食"));

        // 汤羹
        allFoodItems.add(new FoodItem("酸辣汤",
            "酸辣开胃，配料丰富。",
            25.00, "汤羹"));

        allFoodItems.add(new FoodItem("西湖牛肉羹",
            "鲜嫩滑润，营养丰富。",
            32.00, "汤羹"));

        allFoodItems.add(new FoodItem("银耳莲子羹",
            "清甜滋润，养生佳品。",
            18.00, "汤羹"));

        // 饮品
        allFoodItems.add(new FoodItem("酸梅汤",
            "消暑解渴，酸甜可口。",
            12.00, "饮品"));

        allFoodItems.add(new FoodItem("豆浆",
            "现磨豆浆，营养健康。",
            8.00, "饮品"));

        allFoodItems.add(new FoodItem("菊花茶",
            "清热降火，清香怡人。",
            10.00, "饮品"));

        currentFoodItems = new ArrayList<>(allFoodItems);
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
