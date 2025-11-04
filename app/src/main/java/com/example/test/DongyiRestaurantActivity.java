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

public class DongyiRestaurantActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private Button cartButton;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private List<FoodItem> currentFoodItems;
    private ShoppingCart shoppingCart;

    private TextView catNoodles, catMeals, catSteamedFried;
    private TextView currentSelectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongyi_restaurant);

        shoppingCart = ShoppingCart.getInstance();

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
        allFoodItems = new ArrayList<>();

        // 板块1：面食系列
        allFoodItems.add(new FoodItem("川味担担面",
            "经典川味面食，芝麻酱香浓郁，花生碎增添口感，微辣开胃。",
            4.00, "面食系列"));

        allFoodItems.add(new FoodItem("武汉热干面",
            "武汉特色早餐，芝麻酱拌面，劲道爽滑，配上榨菜丁和葱花。",
            2.50, "面食系列"));

        allFoodItems.add(new FoodItem("炸酱面",
            "老北京风味，肉酱浓香，配黄瓜丝和豆芽，咸香适口。",
            4.00, "面食系列"));

        allFoodItems.add(new FoodItem("香辣牛肉卤面",
            "精选牛肉块，卤汁浓郁，香辣入味，配软烂牛肉和青菜。",
            12.00, "面食系列"));

        allFoodItems.add(new FoodItem("牛肉拉面",
            "手工拉制，面条劲道，牛肉汤底浓香，配炖煮牛肉片。",
            6.00, "面食系列"));

        // 板块2：套餐系列
        allFoodItems.add(new FoodItem("叉烧套餐",
            "广式叉烧，色泽红亮，甜咸适中，配米饭和时蔬，营养均衡。",
            13.00, "套餐系列"));

        allFoodItems.add(new FoodItem("烤鸡套餐",
            "整只烤鸡腿，外焦里嫩，香气扑鼻，配米饭、青菜和例汤。",
            12.00, "套餐系列"));

        allFoodItems.add(new FoodItem("鸭腿套餐",
            "卤制鸭腿，肉质鲜嫩，咸香入味，搭配米饭和时令蔬菜。",
            10.00, "套餐系列"));

        allFoodItems.add(new FoodItem("鸡腿套餐",
            "香煎鸡腿，皮脆肉嫩，汁水丰富，配米饭、蔬菜和汤品。",
            12.00, "套餐系列"));

        allFoodItems.add(new FoodItem("鹅腿套餐",
            "卤鹅腿，肉质紧实，香味浓郁，搭配米饭和小菜，饱腹感强。",
            11.00, "套餐系列"));

        allFoodItems.add(new FoodItem("孜然肉片套餐",
            "孜然羊肉片，香辣可口，配洋葱青椒，附米饭和蔬菜。",
            10.00, "套餐系列"));

        allFoodItems.add(new FoodItem("鸡排套餐",
            "炸鸡排，外酥里嫩，金黄诱人，配米饭、沙拉和玉米浓汤。",
            10.00, "套餐系列"));

        allFoodItems.add(new FoodItem("红烧肉套餐",
            "家常红烧肉，肥而不腻，入口即化，色泽红亮，配米饭和青菜。",
            15.00, "套餐系列"));

        // 板块3：蒸炸系列
        allFoodItems.add(new FoodItem("酥饼",
            "传统武汉酥饼，层层酥脆，内馅咸香，刚出炉最好吃。",
            2.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("面窝",
            "武汉特色小吃，外酥内软，中空造型，配豆浆最佳。",
            2.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("牛肉馅饼",
            "现做现卖，牛肉馅料丰富，外皮金黄酥脆，肉汁饱满。",
            4.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("蒸饺",
            "手工蒸饺，皮薄馅大，鲜香多汁，蘸醋更美味。",
            5.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("油条",
            "传统早餐，炸至金黄，外酥内软，配豆浆或粥都好吃。",
            2.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("酱肉包",
            "精选猪肉馅，酱香浓郁，皮薄馅多，热气腾腾。",
            1.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("粉条肉沫包",
            "粉条配肉沫，口感丰富，咸鲜适口，物美价廉。",
            1.00, "蒸炸系列"));

        allFoodItems.add(new FoodItem("韭菜鸡蛋包",
            "素馅包子，韭菜鸡蛋，鲜香扑鼻，清淡营养。",
            0.80, "蒸炸系列"));

        allFoodItems.add(new FoodItem("虾仁包",
            "鲜虾仁馅，Q弹美味，皮软馅鲜，配料讲究。",
            1.20, "蒸炸系列"));

        allFoodItems.add(new FoodItem("蒸鸡蛋",
            "嫩滑蒸蛋，入口即化，营养丰富，老少皆宜。",
            0.80, "蒸炸系列"));

        currentFoodItems = new ArrayList<>(allFoodItems);
    }

    private void setupRecyclerView() {
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemAdapter = new FoodItemAdapter(currentFoodItems, foodItem -> {
            shoppingCart.addItem(foodItem);
            Toast.makeText(DongyiRestaurantActivity.this,
                foodItem.getName() + " 已加入购物车！",
                Toast.LENGTH_SHORT).show();
            updateCartButton();
        });
        foodRecyclerView.setAdapter(foodItemAdapter);
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
        updateCartButton();
    }
}
