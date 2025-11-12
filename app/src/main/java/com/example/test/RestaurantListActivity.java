package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class RestaurantListActivity extends AppCompatActivity {

    private EditText searchBar;
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

        // 设置搜索框监听
        setupSearchBar();

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
        }); // <-- 添加了这个 });



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

    private void setupSearchBar() {
        // 监听搜索框的回车键
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                return true;
            }
            return false;
        });

        // 也可以设置图标点击事件（如果需要）
        searchBar.setOnClickListener(v -> {
            // 当用户点击搜索框时的处理
        });
    }

    private void performSearch() {
        String keyword = searchBar.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳转到搜索结果页面
        Intent intent = new Intent(RestaurantListActivity.this, SearchResultsActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }
}
