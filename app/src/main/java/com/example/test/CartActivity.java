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

        totalPriceText.setText(String.format(Locale.US, "$%.2f", shoppingCart.getTotalPrice()));
        cartAdapter.notifyDataSetChanged();
    }

    private void checkout() {
        if (shoppingCart.getItems().isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderSummary = String.format(Locale.US,
            "Order placed successfully!\nTotal: $%.2f\nItems: %d",
            shoppingCart.getTotalPrice(),
            shoppingCart.getItemCount());

        Toast.makeText(this, orderSummary, Toast.LENGTH_LONG).show();

        shoppingCart.clear();
        updateUI();

        // Optionally, go back to main activity after a delay
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
