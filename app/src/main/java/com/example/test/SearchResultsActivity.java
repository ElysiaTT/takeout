package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private Button backButton;
    private TextView searchKeywordText;
    private RecyclerView resultsRecyclerView;
    private LinearLayout emptyView;
    private SearchResultAdapter searchResultAdapter;
    private List<FoodItem> searchResults;
    private FoodDatabaseHelper databaseHelper;
    private ShoppingCart shoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // 初始化视图
        backButton = findViewById(R.id.backButton);
        searchKeywordText = findViewById(R.id.searchKeywordText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // 初始化数据库和购物车
        databaseHelper = new FoodDatabaseHelper(this);
        shoppingCart = ShoppingCart.getInstance();

        // 获取搜索关键词
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            searchKeywordText.setText("搜索：" + keyword);
            performSearch(keyword);
        } else {
            searchKeywordText.setText("搜索结果");
            showEmptyView();
        }

        // 返回按钮
        backButton.setOnClickListener(v -> finish());

        // 设置RecyclerView
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void performSearch(String keyword) {
        // 在数据库中搜索
        searchResults = databaseHelper.searchFoodItems(keyword);

        if (searchResults != null && !searchResults.isEmpty()) {
            // 显示搜索结果
            searchResultAdapter = new SearchResultAdapter(searchResults, foodItem -> {
                // 添加到购物车
                shoppingCart.addItem(foodItem);
                Toast.makeText(SearchResultsActivity.this,
                        foodItem.getName() + " 已加入购物车！",
                        Toast.LENGTH_SHORT).show();
            });
            resultsRecyclerView.setAdapter(searchResultAdapter);
            resultsRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            // 显示空状态
            showEmptyView();
        }
    }

    private void showEmptyView() {
        resultsRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
