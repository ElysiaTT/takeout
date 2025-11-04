package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RestaurantListActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button filterButton;
    private CardView chineseRestaurantCard;
    private CardView italianRestaurantCard;
    private CardView burgerRestaurantCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        searchBar = findViewById(R.id.searchBar);
        filterButton = findViewById(R.id.filterButton);
        chineseRestaurantCard = findViewById(R.id.chineseRestaurantCard);
        italianRestaurantCard = findViewById(R.id.italianRestaurantCard);
        burgerRestaurantCard = findViewById(R.id.burgerRestaurantCard);

        // 设置餐厅卡片点击事件
        chineseRestaurantCard.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, ChineseRestaurantActivity.class);
            startActivity(intent);
        });

        italianRestaurantCard.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, ItalianRestaurantActivity.class);
            startActivity(intent);
        });

        burgerRestaurantCard.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, BurgerRestaurantActivity.class);
            startActivity(intent);
        });

        // 筛选按钮
        filterButton.setOnClickListener(v -> {
            Toast.makeText(this, "筛选功能：评分、配送时间、人均消费", Toast.LENGTH_SHORT).show();
        });

        // 底部导航栏
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Toast.makeText(this, "首页", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.navOrders).setOnClickListener(v -> {
            Toast.makeText(this, "订单页面", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Toast.makeText(this, "我的页面", Toast.LENGTH_SHORT).show();
        });
    }
}
