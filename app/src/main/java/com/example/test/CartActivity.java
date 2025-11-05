package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView totalPriceText;
    private TextView emptyCartText;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        shoppingCart = ShoppingCart.getInstance();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        emptyCartText = findViewById(R.id.emptyCartText);
        checkoutButton = findViewById(R.id.checkoutButton);

        setupRecyclerView();
        updateUI();

        checkoutButton.setOnClickListener(v -> checkout());
    }

    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(shoppingCart.getItems(), new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                shoppingCart.updateQuantity(item.getFoodItem(), newQuantity);
                updateUI();
            }

            @Override
            public void onRemoveItem(CartItem item) {
                shoppingCart.removeItem(item.getFoodItem());
                updateUI();
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void updateUI() {
        if (shoppingCart.getItems().isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
        }

        totalPriceText.setText(String.format(Locale.getDefault(), "¥%.2f", shoppingCart.getTotalPrice()));
        cartAdapter.notifyDataSetChanged();
    }

    private void checkout() {
        if (shoppingCart.getItems().isEmpty()) {
            Toast.makeText(this, "购物车是空的！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建订单
        // 注意：这里需要获取餐厅名称，实际应该从购物车中获取
        // 目前简化处理，使用"外卖餐厅"作为默认值
        // 在实际项目中，应该在添加商品到购物车时记录餐厅信息
        String restaurantName = getRestaurantNameFromCart();

        OrderManager orderManager = OrderManager.getInstance();
        Order order = orderManager.createOrder(restaurantName, shoppingCart.getItems());

        String orderSummary = String.format(Locale.getDefault(),
            "订单提交成功！\n订单号：%s\n总金额：¥%.2f\n商品数：%d",
            order.getOrderId(),
            shoppingCart.getTotalPrice(),
            shoppingCart.getItemCount());

        Toast.makeText(this, orderSummary, Toast.LENGTH_LONG).show();

        shoppingCart.clear();
        updateUI();

        // 返回主页
        finish();
    }

    /**
     * 从购物车获取餐厅名称
     * 这里简化处理，实际应该在购物车中存储餐厅信息
     */
    private String getRestaurantNameFromCart() {
        // 简化处理：返回默认餐厅名
        // 实际项目中应该在ShoppingCart中添加餐厅信息
        return "外卖餐厅";
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
