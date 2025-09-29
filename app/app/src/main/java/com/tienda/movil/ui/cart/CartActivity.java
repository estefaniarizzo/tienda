

package com.tienda.movil.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tienda.movil.R;
import com.tienda.movil.data.Product;
import com.tienda.movil.ui.products.ProductsActivity;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private Button btnCheckout, btnBackToProducts;
    private TextView tvTotal;
    private CartAdapter adapter;
    private List<Product> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        rvCart = findViewById(R.id.rvCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvTotal = findViewById(R.id.tvTotal);
        btnBackToProducts = findViewById(R.id.btnBackToProducts);
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        cartList.addAll(CartSingleton.getInstance().getCart());
        adapter = new CartAdapter(cartList, new CartAdapter.OnCartClickListener() {
            @Override
            public void onRemove(Product product) {
                CartSingleton.getInstance().removeFromCart(product);
                cartList.clear();
                cartList.addAll(CartSingleton.getInstance().getCart());
                adapter.notifyDataSetChanged();
                updateTotal();
                Toast.makeText(CartActivity.this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
            }
        });
        rvCart.setAdapter(adapter);
        updateTotal();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartSingleton.getInstance().clearCart();
                cartList.clear();
                adapter.notifyDataSetChanged();
                updateTotal();
                Toast.makeText(CartActivity.this, "¡Compra finalizada!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackToProducts.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, ProductsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void updateTotal() {
        double total = 0;
        for (Product p : cartList) total += p.price;
        tvTotal.setText("Total: $" + total);
    }
}

class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    interface OnCartClickListener {
        void onRemove(Product product);
    }
    private List<Product> products;
    private OnCartClickListener listener;
    CartAdapter(List<Product> products, OnCartClickListener listener) {
        this.products = products;
        this.listener = listener;
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CartViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product p = products.get(position);
        holder.tvName.setText(p.name);
        holder.tvDesc.setText(p.description);
        holder.tvPrice.setText("$" + p.price);
        holder.btnAddToCart.setVisibility(View.GONE);
        holder.itemView.setOnLongClickListener(v -> { listener.onRemove(p); return true; });
    }
    @Override
    public int getItemCount() { return products.size(); }
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice;
        Button btnAddToCart;
        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDesc = itemView.findViewById(R.id.tvProductDesc);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}

class CartSingleton {
    private static CartSingleton INSTANCE;
    private List<Product> cart = new ArrayList<>();
    private CartSingleton() {}
    static CartSingleton getInstance() {
        if (INSTANCE == null) INSTANCE = new CartSingleton();
        return INSTANCE;
    }
    List<Product> getCart() { return cart; }
    void addToCart(Product p) { cart.add(p); }
    void removeFromCart(Product p) { cart.remove(p); }
    void clearCart() { cart.clear(); }
}
