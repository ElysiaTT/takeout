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
    private CardView dongyiRestaurantCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        searchBar = findViewById(R.id.searchBar);
        chineseRestaurantCard = findViewById(R.id.chineseRestaurantCard);
        italianRestaurantCard = findViewById(R.id.italianRestaurantCard);
        burgerRestaurantCard = findViewById(R.id.burgerRestaurantCard);
        dongyiRestaurantCard = findViewById(R.id.dongyiRestaurantCard);

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

        dongyiRestaurantCard.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, DongyiRestaurantActivity.class);
            startActivity(intent);
        });



        // 底部导航栏
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Toast.makeText(this, "首页", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.navOrders).setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
