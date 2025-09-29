
package com.tienda.movil.ui.cart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tienda.movil.R;
import com.tienda.movil.data.Product;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView rvCart;
    private Button btnCheckout;
    private CartAdapter adapter;
    private List<Product> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        rvCart = findViewById(R.id.rvCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        // Simulación: el carrito se pasa por intent o se obtiene de un singleton. Aquí se simula con datos estáticos.
        cartList.addAll(CartSingleton.getInstance().getCart());
        adapter = new CartAdapter(cartList, new CartAdapter.OnCartClickListener() {
            @Override
            public void onRemove(Product product) {
                CartSingleton.getInstance().removeFromCart(product);
                cartList.clear();
                cartList.addAll(CartSingleton.getInstance().getCart());
                adapter.notifyDataSetChanged();
                Toast.makeText(CartActivity.this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show();
            }
        });
        rvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartSingleton.getInstance().clearCart();
                cartList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(CartActivity.this, "¡Compra finalizada!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

// Adaptador para RecyclerView
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
        View v = android.view.LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new CartViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product p = products.get(position);
        ((android.widget.TextView) holder.itemView.findViewById(android.R.id.text1)).setText(p.name + " ($" + p.price + ")");
        ((android.widget.TextView) holder.itemView.findViewById(android.R.id.text2)).setText(p.description);
        holder.itemView.setOnLongClickListener(v -> { listener.onRemove(p); return true; });
    }
    @Override
    public int getItemCount() { return products.size(); }
    static class CartViewHolder extends RecyclerView.ViewHolder {
        CartViewHolder(@NonNull View itemView) { super(itemView); }
    }
}

// Singleton para carrito
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
