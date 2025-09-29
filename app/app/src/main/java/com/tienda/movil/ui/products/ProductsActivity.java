
package com.tienda.movil.ui.products;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tienda.movil.R;
import com.tienda.movil.data.AppDatabase;
import com.tienda.movil.data.Product;
import com.tienda.movil.data.ProductDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView rvProducts;
    private FloatingActionButton fabAddProduct;
    private ProductAdapter adapter;
    private ProductDao productDao;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        rvProducts = findViewById(R.id.rvProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));

        productDao = AppDatabaseSingleton.getInstance(this).productDao();
        loadProducts();

        adapter = new ProductAdapter(productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEdit(Product product) {
                showProductDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                productDao.delete(product);
                loadProducts();
                Toast.makeText(ProductsActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        rvProducts.setAdapter(adapter);

        fabAddProduct.setOnClickListener(v -> showProductDialog(null));
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(productDao.getAll());
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void showProductDialog(Product product) {
        boolean isEdit = product != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Editar producto" : "Agregar producto");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null);
        EditText etName = view.findViewById(R.id.etProductName);
        EditText etDesc = view.findViewById(R.id.etProductDesc);
        EditText etPrice = view.findViewById(R.id.etProductPrice);
        if (isEdit) {
            etName.setText(product.name);
            etDesc.setText(product.description);
            etPrice.setText(String.valueOf(product.price));
        }
        builder.setView(view);
        builder.setPositiveButton(isEdit ? "Actualizar" : "Agregar", (dialog, which) -> {
            String name = etName.getText().toString();
            String desc = etDesc.getText().toString();
            String priceStr = etPrice.getText().toString();
            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Nombre y precio requeridos", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            if (isEdit) {
                product.name = name;
                product.description = desc;
                product.price = price;
                productDao.update(product);
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Product newProduct = new Product();
                newProduct.name = name;
                newProduct.description = desc;
                newProduct.price = price;
                productDao.insert(newProduct);
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
            }
            loadProducts();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}

// Adaptador para RecyclerView
class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    interface OnProductClickListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }
    private List<Product> products;
    private OnProductClickListener listener;
    ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ProductViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = products.get(position);
        ((TextView) holder.itemView.findViewById(android.R.id.text1)).setText(p.name + " ($" + p.price + ")");
        ((TextView) holder.itemView.findViewById(android.R.id.text2)).setText(p.description);
        holder.itemView.setOnClickListener(v -> listener.onEdit(p));
        holder.itemView.setOnLongClickListener(v -> { listener.onDelete(p); return true; });
    }
    @Override
    public int getItemCount() { return products.size(); }
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductViewHolder(@NonNull View itemView) { super(itemView); }
    }
}

// Singleton para Room
class AppDatabaseSingleton {
    private static AppDatabase INSTANCE;
    static AppDatabase getInstance(android.content.Context context) {
        if (INSTANCE == null) {
            INSTANCE = androidx.room.Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "tienda-db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
