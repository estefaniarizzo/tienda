
package com.tienda.movil.ui.clients;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.tienda.movil.data.Client;
import com.tienda.movil.data.ClientDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ClientsActivity extends AppCompatActivity {
    private RecyclerView rvClients;
    private FloatingActionButton fabAddClient;
    private ClientAdapter adapter;
    private ClientDao clientDao;
    private List<Client> clientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
        rvClients = findViewById(R.id.rvClients);
        fabAddClient = findViewById(R.id.fabAddClient);
        rvClients.setLayoutManager(new LinearLayoutManager(this));

        clientDao = AppDatabaseSingleton.getInstance(this).clientDao();
        loadClients();

        adapter = new ClientAdapter(clientList, new ClientAdapter.OnClientClickListener() {
            @Override
            public void onEdit(Client client) {
                showClientDialog(client);
            }

            @Override
            public void onDelete(Client client) {
                clientDao.delete(client);
                loadClients();
                Toast.makeText(ClientsActivity.this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        rvClients.setAdapter(adapter);

        fabAddClient.setOnClickListener(v -> showClientDialog(null));
    }

    private void loadClients() {
        clientList.clear();
        clientList.addAll(clientDao.getAll());
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void showClientDialog(Client client) {
        boolean isEdit = client != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Editar cliente" : "Agregar cliente");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_client, null);
        EditText etName = view.findViewById(R.id.etClientName);
        EditText etEmail = view.findViewById(R.id.etClientEmail);
        EditText etPhone = view.findViewById(R.id.etClientPhone);
        if (isEdit) {
            etName.setText(client.name);
            etEmail.setText(client.email);
            etPhone.setText(client.phone);
        }
        builder.setView(view);
        builder.setPositiveButton(isEdit ? "Actualizar" : "Agregar", (dialog, which) -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Nombre y email requeridos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEdit) {
                client.name = name;
                client.email = email;
                client.phone = phone;
                clientDao.update(client);
                Toast.makeText(this, "Cliente actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Client newClient = new Client();
                newClient.name = name;
                newClient.email = email;
                newClient.phone = phone;
                clientDao.insert(newClient);
                Toast.makeText(this, "Cliente agregado", Toast.LENGTH_SHORT).show();
            }
            loadClients();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
}

// Adaptador para RecyclerView
class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {
    interface OnClientClickListener {
        void onEdit(Client client);
        void onDelete(Client client);
    }
    private List<Client> clients;
    private OnClientClickListener listener;
    ClientAdapter(List<Client> clients, OnClientClickListener listener) {
        this.clients = clients;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ClientViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client c = clients.get(position);
        ((android.widget.TextView) holder.itemView.findViewById(android.R.id.text1)).setText(c.name + " (" + c.email + ")");
        ((android.widget.TextView) holder.itemView.findViewById(android.R.id.text2)).setText(c.phone);
        holder.itemView.setOnClickListener(v -> listener.onEdit(c));
        holder.itemView.setOnLongClickListener(v -> { listener.onDelete(c); return true; });
    }
    @Override
    public int getItemCount() { return clients.size(); }
    static class ClientViewHolder extends RecyclerView.ViewHolder {
        ClientViewHolder(@NonNull View itemView) { super(itemView); }
    }
}

// Singleton para Room
class AppDatabaseSingleton {
    private static com.tienda.movil.data.AppDatabase INSTANCE;
    static com.tienda.movil.data.AppDatabase getInstance(android.content.Context context) {
        if (INSTANCE == null) {
            INSTANCE = androidx.room.Room.databaseBuilder(context.getApplicationContext(), com.tienda.movil.data.AppDatabase.class, "tienda-db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
